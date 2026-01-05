#Ended up being a while loop😂
print("Ordinal Suffix")

userInput = ""
exitCode = "exit"

while userInput != exitCode.lower():
    print("Enter a Soldier Squadron regiment number!")
    print("Enter \"exit\" to quit program or any value to continue:")
    userInput = input(">: ")
    if userInput != exitCode.lower():
        lastValueCheck = (int(userInput[-1]))
    
        if lastValueCheck == 1:
            print(userInput + "st Division")
        elif lastValueCheck == 2:
            print(userInput + "nd Division")
        elif lastValueCheck == 3:
            print(userInput + "rd Division")
        elif  lastValueCheck <= 9:
            print(userInput + "th Division")
        else:
            print("No oridinal sufix for this.")
        
    if userInput == exitCode.lower():
        print("thanks for trying")
        break