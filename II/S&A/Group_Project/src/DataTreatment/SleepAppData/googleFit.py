# Tratamento dos dados da GoogleFit

import json
from datetime import datetime, timedelta
from pprint import pprint




def main(sleepfile):

    f = open(sleepfile)
    data = json.load(f)
        
    # 1- Awake (during sleep cycle)
    # 2- Sleep
    # 3- Out-of-bed
    # 4- Light sleep
    # 5- Deep sleep
    # 6- REM

    # {'dia1': [DataInicio, DataFim, sleep1, sleep2, sleep3, sleep4, sleep5, sleep6, H_Total] } i
    # {'dia2': [DataInicio, DataFim, sleep1, sleep2, sleep3, sleep4, sleep5, sleep6, H_Total] } i+1
    # {'dia3': [DataInicio, DataFim, sleep1, sleep2, sleep3, sleep4, sleep5, sleep6, H_Total] }

    data_dict = dict() # inicialização do dict
    dia = 1
    DataFim = datetime.min
    
    for i in data['Data Points']:
        
        # Inicializar variaveis com dados da pulseira
        sleepType, duracao, dt_start, dt_end = getData(i)
     

        # Condicao Criar [ Lista ]
        if(DataFim == datetime.min) :
            listaAux = [dt_start,datetime.min,timedelta(0),timedelta(0),timedelta(0),timedelta(0),timedelta(0),timedelta(0),timedelta(0)]
        # Condicao Adicionar [ Dicionario ]     
        elif ((dt_start != DataFim) or (i ==  data['Data Points'][-1])): # so para meter data inicio  DataFim == 0
            
            if i ==  data['Data Points'][-1]:
                listaAux[1] = dt_end
            else:
                listaAux[1] = DataFim
            DataFim = datetime.min

            data_dict[dia] = listaAux  # !!
            dia = dia + 1
            listaAux = [dt_start,datetime.min,timedelta(0),timedelta(0),timedelta(0),timedelta(0),timedelta(0),timedelta(0),timedelta(0)]
        

        # Adicionar Duracao Sleep [Lista]
        listaAux[sleepType+1] = listaAux[sleepType+1] + duracao
        if ((sleepType != 1) and (sleepType != 3)):
            listaAux[8] = listaAux[8] + duracao

        # Proxima Iteracao
        DataFim = dt_end
        
        
        #printData(sleepType,duracao,dt_start,dt_end)
    #pprint(data_dict)
    dictFinal = {}
    datas = []
    counter = 1
    for i in data_dict.values():
        if ((i[0].day <= 24 and i[0].month == 4 and i[0].hour <= 19) or (i[0].day <= 23 and i[0].month == 4) and i[0].year == 2022):
            
            pass
            
        else:
            datas.append(i[0])
            dictFinal[counter] = i
            counter += 1

    return dictFinal, datas

    
def printData(sleepType, duracao, dt_start, dt_end):
    startString = dt_start.strftime('%Y-%m-%d %H:%M:%S')
    endString = dt_end.strftime('%Y-%m-%d %H:%M:%S')
    
    print("Sleep: ", sleepType)
    print("Duração: ", duracao)
    print("Start time: ", startString)
    print("End time: ", endString)


def getData(i):
    sleepType = i['fitValue'][0]['value']['intVal'] #1-6
    startTimestamp = i['startTimeNanos'] #'%Y-%m-%d %H:%M:%S'
    endTimestamp = i['endTimeNanos']     #'%Y-%m-%d %H:%M:%S'
        
    dt_start = datetime.fromtimestamp(startTimestamp // 1000000000)
    dt_end = datetime.fromtimestamp(endTimestamp // 1000000000)
    duracao = dt_end - dt_start
    #print(dt_end)

    return sleepType, duracao, dt_start, dt_end
    

#if __name__ == "__main__":
#    main()