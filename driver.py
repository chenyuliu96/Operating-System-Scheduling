import sys,os

if __name__ == "__main__":
    if(len(sys.argv) <3):
        exit(1)

    verboseFlag = False
    inputFile = None
    schedule = None

    if(len(sys.argv)==3):
        inputFile = sys.argv[2]
        schedule = sys.argv[1].upper()
    
    
    if(len(sys.argv) == 4):
        inputFile = sys.argv[2]
        schedule = sys.argv[1].upper()
        verboseFlag = True


    if(verboseFlag):
        if(schedule == "FCFS"):
            os.system("java Solution_FCFS -verbose "+ inputFile)
        elif(schedule == "RR"):
            os.system("java Solution_RR -verbose "+ inputFile)
        elif(schedule == "SJF"):
            os.system("java Solution_SJF -verbose "+ inputFile)
        elif(schedule == "UNI"):
            os.system("java Solution_Uniprocessor -verbose "+ inputFile)
    else:
        if(schedule == "FCFS"):
            os.system("java Solution_FCFS "+ inputFile)
        elif(schedule == "RR"):
            os.system("java Solution_RR "+ inputFile)
        elif(schedule == "SJF"):
            os.system("java Solution_SJF "+ inputFile)
        elif(schedule == "UNI"):
            os.system("java Solution_Uniprocessor "+ inputFile)
