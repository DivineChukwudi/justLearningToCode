#   index operator [] = gives access to a sequence's element (str, list, tuples)

name = 'Divine Chukwudi!'


#if name[0].islower():
#    print("first letter is lowercase")
#else:
#    print("first letter is uppercase")
    

firstName = name[:7]
lastName = name[7:]
lastValue = name[-1] #if you want to take the letters from negative add a decreasing negative index step count
print(lastName.upper())
print(lastValue)