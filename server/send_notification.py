import json;
import requests;

URL = "https://fcm.googleapis.com/fcm/send"

def sendMessage(message, to):
    headers = {
        'Content-Type':'application/json',
        'Authorization':'key=AIzaSyBO3CF2iSY0t0Q_v96KfBFtzuWE909mC6k'
    }
    data = {
        'to': to,
        'notification': {
            'title' : 'Health Boarding Pass',
            'body' : message
        }
    }
    response = requests.post(URL, data=json.dumps(data), headers=headers);
    return response;

