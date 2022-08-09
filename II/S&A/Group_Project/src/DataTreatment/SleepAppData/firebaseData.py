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
import pytz

utc=pytz.UTC

# {'dia1': [HorasAtividade, [brilho], [luz]], Data Final }
# {'dia2': [HorasAtividade, [brilho], [luz]], Data Final }
# {'dia3': [HorasAtividade, [brilho], [luz]], Data Final }

# Inicio -> Rating 

aux = {}

def main(credFile, projectIDName, dataAdormeceu):
  
  # Dados em [ Objetos/Structs/Dicionarios ] provientes do Firebase
  usageData1, ratings1 = getDataDict(credFile, projectIDName)

  usageData = []
  ratings = []

  for i in ratings1:
    if (i['Date'].day <= 24 and i['Date'].month == 4 and i['Date'].year == 2022):
      pass
    else:
      ratings.append(i)

  for i in usageData1:
    if ((i['Date'].day <= 24 and i['Date'].month == 4 and i['Date'].hour <= 19) or (i['Date'].day <= 23 and i['Date'].month == 4) and i['Date'].year == 2022):
      pass
    else:
      usageData.append(i)   
  
  
  dia = 1
  dict_final = {}
  list = [timedelta(0),[],[], datetime.min, timedelta(0), None, [], [], None]
  
  initTime = False # Data para calcular intervalo 
  duracao = timedelta(0)
  dates_list = []

  previousScreenOn = True

  dataAdormeceuCounter = 0

  # preencher sem duplicados

  day = ratings[0]['Date'].day 
  dates_list.append(ratings[0]['Date'])
  
  for i in ratings:
    if (i['Date'].day != day): 
      dates_list.append(i['Date'])
    day = i['Date'].day


  for j in usageData:

    #Se a data do usage for superior a data do rating.Verificar se o dia ja "acabou"
    
    if j['Date'] > dates_list[dia-1]: # dia seguinte
      # guardar no dict
      dict_final[dia] = list
      if list[5] != None:
        dataAdormeceuCounter +=1
      list = [timedelta(0),[],[],datetime.min, timedelta(0), None, [], [], None]
      dia = dia + 1
      initTime = False

    # REMENDOS ?
    if (dia) > len(dates_list):
      break

    # calculo duracao
    if initTime == False:
      duracao = timedelta(0)
    else:
      duracao = j['Date'] - initTime
  

    initTime = j['Date']
    # Preencher Lista 
    if (previousScreenOn == True):

      if dataAdormeceuCounter < len(dataAdormeceu):
        usageRealTime = j['Date'] + timedelta(hours=1) #somar uma hora porque o GMT não está bem
        intervalDate = dataAdormeceu[dataAdormeceuCounter] - timedelta(hours=2) # data duas horas antes de adormecer
        intervalDate = utc.localize(intervalDate)
        dateSleep = utc.localize(dataAdormeceu[dataAdormeceuCounter] + timedelta(hours=1)) # Dar uma tolerancia de uma hora
        
        if (usageRealTime < dateSleep and usageRealTime > intervalDate):
          list[4] = list[4] + duracao
          list[5] = dataAdormeceu[dataAdormeceuCounter]
          list[6].append(j['Brightness'])
          list[7].append(j['Light'])

      list[0] = list[0] + duracao
      
    previousScreenOn = j['ScreenOn']
      
    list[1].append(j['Brightness'])
    list[2].append(j['Light'])
    list[3] = dates_list[dia-1]
    list[8] = ratings[dia-1]

    
    # Caso [Ultimo elemento]
    if j == usageData[-1]:
      dict_final[dia] = list

    #print(initTime)
  #print(dict_final)

  # Escolha da metrica - Media

  res_dict = {}
  
  for key, value in dict_final.items():
    mediaBrilho = statistics.median(value[1]) # Mediana
    mediaLuz = statistics.median(value[2])    # MÉDIANA
    
    mediaBrilho2h = None
    mediaLuz2h = None
    if (value[6] and value[7]):
      mediaBrilho2h = statistics.median(value[6]) # Mediana
      mediaLuz2h = statistics.median(value[7])    # MÉDIANA
    res_dict[key] = [value[0],mediaBrilho,mediaLuz,value[3], value[4], value[5], mediaBrilho2h, mediaLuz2h, value[8]['Rating']]
    
  return res_dict



def getDataDict(credFile, projectIDName):
  collectionName = 'UsageStats'
  collectionName1 = 'RatingStats'

  cred = credentials.Certificate(credFile)

  firebase_admin.initialize_app(cred, {
    'projectId': projectIDName,
  })

  # UsageStats
  db = firestore.client()
  items = db.collection(collectionName)
  usageData = items.stream()

  # RatingStats
  db1 = firestore.client()
  items1 = db1.collection(collectionName1)
  ratings = items1.stream()

  # Obter dados do telemovel
  list = []
  for doc in usageData:
    data = doc.to_dict()
    list.append(data)
    
  # Ordenar por data
  usageDataList = sorted(list, key=lambda d: d['Date']) 

  list1 = []
  for doc1 in ratings:
    data1 = doc1.to_dict()
    list1.append(data1)
    
  # Ordenar por data
  ratingList = sorted(list1, key=lambda d: d['Date'])
  return usageDataList, ratingList




