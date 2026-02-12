import time

myTime = int(input("Enter time in seconds: "))

for x in range(0, myTime):
    print(myTime)
    time.sleep(2)
    myTime = myTime - 1

print("YIPEEEE!!!!")