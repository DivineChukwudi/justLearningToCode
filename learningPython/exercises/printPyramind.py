# Draw a Pyramid using *
symbol = "*"
columnValue = 1

#for i in range(10):
#    for j in range(columnValue):
#        print(symbol, end="")
#    columnValue = columnValue + 2
#    print()


for i in range(10):
    for j in range(10 - i):
        print(" ", end="")
        
        #this works like this, for example:
        #when i is 1  and spaces based of the value so initially
        # (9) spaces are represented with "_"
        # _ _ _ _ _ _ _ _ _ *     <------- then an asterisk
        # _ _ _ _ _ _ _ _ * * *
        # _ _ _ _ _ _ _ * * * * *
        # etc...
        
    #for k in range(columnValue):
    #    print(symbol, end="")
    #columnValue = columnValue + 2
    #print()
    
    #Secondary way to represent for loop of k is:
    for k in range(1, 2*i):
        print(symbol, end="")
    print()
        
