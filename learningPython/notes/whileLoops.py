# A while loop: is  statement that wil execute a block of code
#               as long as a given condition is true.

# infinite loop: a loop that never ends. (condition is always true) no way to escape it.

# example of infinite loop
#while 1 == 1:
#    print("infinite loop")


name = input("Enter your name: ").strip()
while name == "":
    print("You didn't enter your name!")
    name = input("Enter your name: ").strip()

print("Hello " + "\"" + name + "\"")

#name = ""
#while len(name) == 0:
#   name = input("Enter your name: ").strip()
#print("Hello " + name)