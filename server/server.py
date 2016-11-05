import threading
import socketserver
import time
import copy
import json
from datetime import datetime
import sys
import json

lock = threading.Lock()

FREE = -1
BOOKED = -2

slots = {}

class Patient(object):
    pass

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

    def handle(self):
        data = self.request.recv(1024).strip()
        with lock:
            print("Connection from {}", self.client_address[0])
            sys.stdout.flush()

        request = Request(data)

        print("action: ", request.action)
        if request.action == 'book':
            with lock:
                book_attempt(request.patientId, request.slotId)
        elif request.action == 'list':
            with lock:
                all_list = slots.values()
        elif request.action == 'test':
            self.reply({"test": "siemanko"})

    def reply(self, response):
        self.request.sendall(bytes(json.dumps(response), 'utf-8'))


def server_thread():
    HOST, PORT = '', 12345
    server = socketserver.TCPServer((HOST, PORT), MyTCPHandler)
    server.serve_forever()

def tick_loop_thread():
    while True:
        time.sleep(1)

def book_attempt(patientId, slotId):
    slot = slots[slotId]
    if not slot.isFree():
        return False
    slot.assignPatient(patientId)
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


if __name__ == "__main__":
    t_server = threading.Thread(target=server_thread)
    t_server.start()
    t_tick_loop = threading.Thread(target=tick_loop_thread)
    t_tick_loop.start()
    s = Slot(datetime.now(), datetime.now())
    print(s.toJSON(0))
