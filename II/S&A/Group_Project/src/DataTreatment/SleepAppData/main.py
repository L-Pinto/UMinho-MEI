import firebaseData
import googleFit
from pprint import pprint


import pandas as pd

jsonpath = 'jsonFiles/'
credPath = 'credFiles/'
csvPath = 'csvFiles/'

def sleepDataToCSV(sleepData, fileName):

    # codigo que mete dados do GoogleFit num csv
    df_googleFit = pd.DataFrame.from_dict(sleepData,
                                            orient='index',
                                            columns=['Start_SleepTime', 'End_SleepTime', 'Awake', 'Sleep','Out_Of_Bed','Light_Sleep','Deep_Sleep','REM','Total_Hours'])

    
    # Convert to csv file
    df_googleFit.to_csv(fileName)
    return df_googleFit


def fireBaseDataToCSV(firebaseData, fileName):

    # codigo que mete dados do Firebase num csv
    df_firebase = pd.DataFrame.from_dict(firebaseData,
                                            orient='index',
                                            columns=['Activity_Time','Brightness','Light','End_Time', 'Activity_Time(last 2 hours)', 'Start_SleepTime', 'Brightness(last 2 hours)','Light(last 2 hours)', 'Sleep quality'])
    
    # Convert to local timezone
    df_firebase['End_Time'] = df_firebase['End_Time'].dt.tz_convert('Europe/Lisbon')


    # Convert to csv file
    df_firebase.to_csv(fileName)

    return df_firebase


def run(precious,sleep,idproj,nome):
    fitSantejo, dataAdormeceu = googleFit.main(jsonpath + sleep)
    appSantejo = firebaseData.main(credPath + precious, idproj, dataAdormeceu)

    #print(dataAdormeceu)

    df_firebase = fireBaseDataToCSV(appSantejo, csvPath + f'firebase_{nome}.csv')
    df_sleepData = sleepDataToCSV(fitSantejo, csvPath + f'googlefit_{nome}.csv')

    output1 = pd.merge(df_firebase, df_sleepData, 
                    on='Start_SleepTime', 
                    how='inner')
                    
    output1.to_csv(csvPath + f'{nome}.csv')

    return output1.to_dict()
    # displaying result
    #print(output1)

#run('preciousSantejo.json','santejo_sleep.json', 'blabla-286b9','santejo')
#run('preciousLuis.json','luis_sleep.json','mysleepcycle','luis')
#run('preciousRaquel.json','raquel_sleep.json','mymetrics-893b8','raquel')
# falta acresentar um argumento (initialize_app()) para conseguirmos correr os 3 ao mesmo tempo


#pprint(fitRaquel)