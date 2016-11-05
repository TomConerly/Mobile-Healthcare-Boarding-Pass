import threading
import socketserver
import time
import copy
import json
from datetime import datetime, timedelta
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

        request = Request(data)

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
        elif request.action == 'cancel_slot':
            with lock:
                response = cancelAttempt(request.patientId, request.slotId)
            if response:
                self.reply({"success": 1})
            else:
                self.reply({"success": 0})
        elif request.action == 'setToken':
            if request.patientId not in patients:
                patients[request.patientId] = Patient(request.patientId)
            patients[request.patientId].token = request.token

def getInterestedSlots(slot):
    result = []
    for s in slots.values():
        if not s.isFree() and s.expectedStartTime > slot.expectedStartTime:
            result.append(s)
    return result

def delaySlot(slotId, newStartTime, newEndTime):
    patientId = slots[slotId].patientId
    if patientId in patients:
        patients[patientId].notify('Your appointment has been delayed. Please confirm new start time in the app.')
    slots[slotId].expectedStartTime = newStartTime
    slots[slotId].expectedEndTime = newEndTime
    slots[slotId].reminded = False

def serverThread():
    HOST, PORT = '', 12345
    server = socketserver.TCPServer((HOST, PORT), MyTCPHandler)
    server.serve_forever()

def tickLoopThread():
    while True:
        time.sleep(10)
        future = datetime.now() + timedelta(minutes=30)
        with lock:
            for slot in slots.values():
                if not slot.isFree() and not slot.reminded and slot.expectedStartTime < future:
                    if slot.patientId in patients:
                        patients[slot.patientId].notify('Your appointment is starting in less than 30 minutes.')
                        slot.reminded = True

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
    for s in getInterestedSlots(slot):
        if s.patientId in patients:
            patients[s.patientId].notify('Earlier appointment became available!')
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
        self.reminded = False

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
    halfhour = timedelta(minutes=30)
    t = datetime(2016, 11, 5, 18)
    s = Slot(t, t + halfhour)
    s.expectedStartTime += halfhour
    s.expectedEndTime += halfhour
    s.assignPatient(1337)
    slots[s.slotId] = s

    random.seed(10)
    for i in range(10):
        t = datetime(2016, 11, 15 + random.randrange(15), random.randrange(9, 18))
        s = Slot(t, t + halfhour)
        s.patientId = FREE
        slots[s.slotId] = s

    for i in range(10):
        t = datetime(2016, 11, 5 + random.randrange(20), random.randrange(9, 18))
        s = Slot(t, t + halfhour)
        s.patientId = BOOKED
        slots[s.slotId] = s

initSlots()
t_server = threading.Thread(target=serverThread)
t_server.start()
t_tick_loop = threading.Thread(target=tickLoopThread)
t_tick_loop.start()
