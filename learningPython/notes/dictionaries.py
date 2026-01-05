#   dictionary = a changeable, unordered collection ofunique key-value pairs
#               fast because they use hashing, allow us to access a value quickly


capitals = {"USA" : "WASHINGTON D.C.",
            "INDIA" : "NEW DELHI",
            "CHINA" : "BEIJING",
            "RUSSIA" : "MOSCOW"}


capitals.update({"GERMANY" : "BERLIN"})
capitals.update({"USA" : "LAS VEGAS"})
capitals.pop("CHINA") #removes key-value pair
capitals.clear()

#print(capitals["CHINA"])
#print(capitals.get('INDIA')) #BETTER WAY TO ACCESS VALUES IN A DICTIONARY
#print(capitals.keys())
#print(capitals.values())
#print(capitals.items())

for key, value in capitals.items():
    print(key, value)

