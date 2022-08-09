#!/usr/bin/env python3

# Instalar modulo do firebase: pip install --upgrade firebase-admin

from datetime import date, datetime, timedelta, timezone
from hashlib import new
from this import d
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from pprint import pprint
import statistics
import firebaseData

jsonpath = 'jsonFiles/'
credPath = 'credFiles/'
csvPath = 'csvFiles/'

def countData(credFile, googlefit, projectIDName):
  counterUsage = 0
  counterRating = 0
  usageData1, ratings1 = firebaseData.getDataDict(credFile, projectIDName)
  usageData = []
  ratings = []

  for i in ratings1:
    if (i['Date'].day <= 24 and i['Date'].month == 4 and i['Date'].year == 2022):
      pass
    else:
      counterRating += 1
      ratings.append(i)

  for i in usageData1:
    if ((i['Date'].day <= 24 and i['Date'].month == 4 and i['Date'].hour <= 19) or (i['Date'].day <= 23 and i['Date'].month == 4) and i['Date'].year == 2022):
      pass
    else:
      counterUsage += 1
      usageData.append(i)

  print("Usage: " + f'{counterUsage}')
  print("Rating: " + f'{counterRating}')
  counterGoogle = countGoogle(googlefit)
  return counterRating, counterUsage, counterGoogle
  


def countGoogle(googleFit):
  num = sum(1 for line in open(googleFit))
  print("Google: " + f'{num}')
  return num

countData(credPath + 'preciousRaquel.json', jsonpath + 'raquel_sleep.json', 'mymetrics-893b8')