def sort(arr):
    newArr = []
    tempArr = arr.copy()   # avoid modifying original list

    while tempArr:
        smallest = tempArr[0]

        for i in tempArr:
            if i < smallest:
                smallest = i

        newArr.append(smallest)
        tempArr.remove(smallest)

    print("Ascending order:", newArr)
arr = []
exitCommand = ""
while exitCommand.lower() != "n":
    
    value = int(input("How many elements?"))
    

    for i in range(value):
        userInput = int(input(f"Value #{i +1}:"))
        arr.append(userInput)

    sort(arr)

    exitCommand = input("cont...?(y/n)")
print(arr)