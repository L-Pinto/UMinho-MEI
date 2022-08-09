import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from csv import reader

# ----------- CSV Columns -----------------
# 1 User_id             12 Total_Minutes 
# 2 Activity_Time       13 Start_Sleep_Day
# 3 Brightness          14 Start_Sleep_Month
# 4 Light               15 Start_Sleep_Hour
# 5 Activity_Time_2H    16 Start_Sleep_Minute 
# 6 Brightness_2H       17 End_Sleep_Day
# 7 Light_2H            18 End_Sleep_Month
# 8 Awake               19 End_Sleep_Hour
# 9 Light_Sleep         20 End_Sleep_Minute
# 10 Deep_Sleep         21 Sleep quality
# 11 REM 


sleepData = {1:{},2:{},3:{}}
activityData = {1:[],2:[],3:[]}
ratingDayData = {1:[],2:[],3:[]}
ratingNumberData = {1:[],2:[],3:[]}



def main(credFile, projectIDName, userid):
    getCSVData()
    cred = credentials.Certificate(credFile)

    firebase_admin.initialize_app(cred, {
        'projectId': projectIDName,
    })


    # Delete old data 
    db1 = firestore.client()
    collection1 = db1.collection("SleepStats")
    delete_collection(collection1, 3)

    db2 = firestore.client()
    collection2 = db2.collection("ActivityStats")
    delete_collection(collection2, 3)

    db3 = firestore.client()
    collection3 = db3.collection("RatingDayStats")
    delete_collection(collection3, 3)

    db4 = firestore.client()
    collection4 = db4.collection("RatingNumberStats")
    delete_collection(collection4, 3)
    

    # Insert sleep stats
    collection1.document().set(sleepData[userid])
    
    # Insert activity stats
    for elem in activityData[userid]:
        collection2.document().set(elem)

    # Insert ratingday stats
    for elem in ratingDayData[userid]:
        collection3.document().set(elem)

    # Insert rating counter stats
    collection4.document().set(ratingNumberData[userid])

    print('Sucessfully saved data')


def getCSVData():
    # skip first line i.e. read header first and then iterate over each row od csv as a list
    with open('csvFiles/finalData.csv', 'r') as read_obj:
        csv_reader = reader(read_obj)
        header = next(csv_reader)
        # Check file as empty
        if header != None:
            # Iterate over each row after the header in the csv
            for row in csv_reader:
                # row variable is a list that represents a row in csv
                addSleepStats(int(row[1]), row)
                addActivityStats(int(row[1]), row)
                addRatingdayStats(int(row[1]), row)
                addRatingnumberStats(int(row[1]), row)


def addSleepStats(userid, row):
    sleepStats = {}
    sleepStats['day'] = int(row[17])
    sleepStats['month'] = int(row[18])
    sleepStats['awake'] = int(row[8])
    sleepStats['lightsleep'] = int(row[9])
    sleepStats['deepsleep'] = int(row[10])
    sleepStats['rem'] = int(row[11])
    sleepData[userid] = sleepStats


def addActivityStats(userid, row):
    atvStats = {}
    atvStats['day'] = int(row[17])
    atvStats['month'] = int(row[18])
    atvStats['activitytime'] = int(row[5])
    activityData[userid].append(atvStats)



def addRatingdayStats(userid, row):
    ratingday = {}
    ratingday['day'] = int(row[17])
    ratingday['month'] = int(row[18])
    ratingday['rating'] = float(row[21])
    ratingDayData[userid].append(ratingday)


def addRatingnumberStats(userid, row):
    if (len(ratingNumberData[userid]) == 0):
        ratingNumberData[userid] = {'r00':0,'r05':0, 'r10':0, 'r15':0, 'r20':0, 'r25':0 ,'r30':0, 'r35':0, 'r40':0, 'r45':0, 'r50': 0}

    s = 'r' + row[21][0] + row[21][2]
    ratingNumberData[userid][s] += 1
    

def delete_collection(coll_ref, batch_size):
    docs = coll_ref.limit(batch_size).stream()
    deleted = 0

    for doc in docs:
        doc.reference.delete()
        deleted = deleted + 1

    if deleted >= batch_size:
        return delete_collection(coll_ref, batch_size)

#main('credFiles/preciousRaquel.json','mymetrics-893b8', 1)
main('credFiles/preciousSantejo.json', 'blabla-286b9', 2)
#main('credFiles/preciousLuis.json','mysleepcycle', 3)
