#Check  if a user input number is a prime
#Prime number is only divisible by 1 and itself.
# 1 is not a prime number
import math

print("***  PRIME NUMBER CHECK  ***")
userValue = int(input("Enter a number: "))
userValueSqrd = int(math.sqrt(userValue))

def primeCheck(userValue):
    if userValue <= 1:
        print(f"{userValue} is not a prime!")
        return
    userValueSqrd = int(math.sqrt(userValue))
    for i in range(2, userValueSqrd + 1):
        if userValue % i == 0:
            print(f"{userValue} is not prime!")
            return
        
    
    print(f"{userValue} is prime!")
    
primeCheck(userValue)


