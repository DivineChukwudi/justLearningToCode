#---------------------------------INDEXING---------------------------------#

# indexing and slicing in Python
#indexing [start:stop:step]

#name = 'Divine Chukwudi'
#firstName = name[0]



#SAME THING
#firstName = name[0:6]
#firstName = name[:6]
#print(firstName)

#SAME THING
#lastName = name[7:15]
#lastName = name[7:]
#print(lastName)


#Slicing with step
#egName = 'Amanda'

#not adding values for start and stop, will take the whole string, python interprets it as [0:len(string)]
#so therefore it take every second letter from the whole string
#everySecondLetter = egName[::2]
#print(everySecondLetter)


#reversing a string
#reversedSting = egName[::-1]
#print(reversedSting)

#---------------------------------INDEXING---------------------------------#

#---------------------------------SLICING---------------------------------#

website = 'http://google.com'
web2 = 'http://spaceX.com'
#[start:stop:step] we seperate with coma
#every character has both a positive and negative index so because of that we can use negative indexing in slicing to take out the '.com' in this manner since -1 = 'm', -2 = 'o', -3 = 'c' and -4 = '.' and so on
slice = slice(7, -4) #7, 13 works the same
print('The domain name is: ' + website[slice])
print('The domain name is: ' + web2[slice])



#---------------------------------SLICING---------------------------------#