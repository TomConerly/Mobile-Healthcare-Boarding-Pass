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
        'data': {
            'message': message,
            'uber': 'yes'
        }
    }
    response = requests.post(URL, data=json.dumps(data), headers=headers);
    return response;
