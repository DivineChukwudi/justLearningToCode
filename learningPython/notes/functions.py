#   functions  = block of code which is executed only when it is called
#def hello(name):
#    print("Hello, " + name + "!")
#    print("This is my first function")
   
#hello("Divine") #output: Hello, Divine!\nThis is my first function
import time


def hello():
    name = input("Enter your name: ")
    surname = input("Enter your surname: ")
    print("Hello, " + name + surname + "!")
    print("This is my first function")
    
    words = ["Wait", "for", "it", "..."]
    
    for i in range(5, 0, -1):
        print(words[i])
        time.sleep(2)
        print("Bomb Venezuela!!!")
   
hello()