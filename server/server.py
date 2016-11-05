import threading
import socketserver
import time
import copy
import json
import datetime
import sys
import json
import send_notification
import random

lock = threading.Lock()

FREE = -1
BOOKED = -2

slots = {}
patients = {}

class Patient(object):
    def __init__(self, patientId):
        self.patientId = patientId
        self.token = ''

    def notify(self, message):
        if self.token != '':
            send_notification.sendMessage(message, self.token)

class Request(object):
    def __init__(self, j):
        self.__dict__ = json.loads(j.decode("utf-8"))


class MyTCPHandler(socketserver.BaseRequestHandler):
    """
    The request handler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """
    def reply(self, response):
        self.request.sendall(bytes(json.dumps(response), 'utf-8'))

    def handle(self):
        data = self.request.recv(1024).strip()
        with lock:
            print("Connection from {}", self.client_address[0])
            sys.stdout.flush()

        request = Request(data)

        print("action: ", request.action)

        if request.action == 'list_slots':
            with lock:
                all_list = slots.values()
            result = []
            for x in all_list:
                result.append(x.toJSON(request.patientId))
            self.reply({"slots": result})
        elif request.action == 'take_slot':
            response = bookAttempt(request.patientId, request.slotId)
            if response:
                self.reply({"success": 1})
            else:
                self.reply({"success": 0})
        elif request.action == 'test':
            self.reply({"test": "asa"})
        elif request.action == 'cancel':
            with lock:
                response = cancelAttempt(request.patientId, request.slotId)
            if response:
                self.request.sendall(bytes("Cancelled successfully!\n", 'utf-8'))
                with lock:
                    for slot in getInterestedSlots(request.slotId):
                        if slot.patientId in patients:
                            patients[slot.patientId].notify('Earlier appointment became available!')
            else:
                self.request.sendall(bytes("Cancellation failed.\n", 'utf-8'))
        elif request.action == 'setToken':
            if request.patientId not in patients:
                patients[request.patientId] = Patient(request.patientId)
            patients[request.patientId].token = request.token

def delaySlot(slotId, newStartTime, newEndTime):
    patientId = slots[slotId].patientId
    if patientId in patients:
        patients[patientId].notify('Your appointment has been delayed. Please confirm new start time in the app.')
    slots[slotId].expectedStartTime = newStartTime
    slots[slotId].expectedEndTime = newEndTime

def serverThread():
    HOST, PORT = '', 12345
    server = socketserver.TCPServer((HOST, PORT), MyTCPHandler)
    server.serve_forever()

def tickLoopThread():
    while True:
        time.sleep(1)

def bookAttempt(patientId, slotId):
    slot = slots[slotId]
    if not slot.isFree():
        return False
    slot.assignPatient(patientId)
    return True

def cancelAttempt(patientId, slotId):
    slot = slots[slotId]
    if slot.patientId != patientId:
        return False
    slot.patientId = FREE
    return True

def isPatientId(x):
    return x >= 0

class Slot(object):
    nextId = 0

    def getNextId():
        result = Slot.nextId
        Slot.nextId += 1
        return result

    def __init__(self, scheduledStartTime, scheduledEndTime):
        self.slotId = Slot.getNextId()
        self.patientId = FREE
        self.scheduledStartTime = scheduledStartTime
        self.expectedStartTime = scheduledStartTime
        self.scheduledEndTime = scheduledEndTime
        self.expectedEndTime = scheduledEndTime
        self.provider = 'Doctor Mc. Doctorface'

    def isFree(self):
        return self.patientId == FREE

    def assignPatient(self, targetPatientId):
        self.patientId = targetPatientId

    def cancel(self):
        self.patientId = FREE

    def toJSON(self, targetPatientId):
        data = {}
        data['slotId'] = self.slotId
        if isPatientId(self.patientId) and self.patientId != targetPatientId:
            data['patientId'] = BOOKED
        else:
            data['patientId'] = self.patientId
        data['scheduledStartTime'] = int(self.scheduledStartTime.timestamp())
        data['expectedStartTime'] = int(self.expectedStartTime.timestamp())
        data['scheduledEndTime'] = int(self.scheduledEndTime.timestamp())
        data['expectedEndTime'] = int(self.expectedEndTime.timestamp())
        data['provider'] = self.provider
        return data

def addSlot(s):
    slots[s.slotId] = s

def initSlots():
    halfhour = datetime.timedelta(minutes=30)
    t = datetime.datetime(2016, 11, 5, 18)
    s = Slot(t, t + halfhour)
    s.expectedStartTime += halfhour
    s.expectedEndTime += halfhour
    s.assignPatient(1337)
    slots[s.slotId] = s

    random.seed(10)
    for i in range(10):
        t = datetime.datetime(2016, 11, 15 + random.randrange(15), random.randrange(9, 18))
        s = Slot(t, t + halfhour)
        s.patientId = FREE
        slots[s.slotId] = s

    for i in range(10):
        t = datetime.datetime(2016, 11, 5 + random.randrange(20), random.randrange(9, 18))
        s = Slot(t, t + halfhour)
        s.patientId = BOOKED
        slots[s.slotId] = s

if __name__ == "__main__":
    initSlots()
    t_server = threading.Thread(target=serverThread)
    t_server.start()
    t_tick_loop = threading.Thread(target=tickLoopThread)
    t_tick_loop.start()
