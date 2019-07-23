#!/bin/python3
import requests

apiKey='8ac75986f5e422fb';
baseurl="localhost:8080"

def get(str, params):
    requests.get(url=(baseurl+str), params=params.update(apiKey=apiKey))


get('/location/'
