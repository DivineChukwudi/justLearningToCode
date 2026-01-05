#nested loop = The "inner loop" wil finish all of its iteratons before
#              executing 1 iteration of the "outer loop"

rows = int(input("How many rows?(Horizontal): "))
columns = int(input("How many columns?(Vertical): "))
symbol = input("Enter symbol or design to use: ")

for i in range (rows):
    for j in range (columns):
        print(symbol, end="")
    print()

#remember indentation is important in nested loops.
# i add the print() function under the inner loop so it printed the symbol vertically without following
#the logic i had in mind
# if i had added the print() function under the outer loop it prints the columns first n times then
# goes to the next line and prints the next row etc.
