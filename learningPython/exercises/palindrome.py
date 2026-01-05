# A string is a palindrome if the reverse of the string is same as the original string
# Example: racecar == racecar(reverse)

# USE:
# 1. UserInput
# 2. Functions
# 3. f-string

print("***  PALINDROME CHECKER  ***")
userString = input("Enter word: ")

def paliDCheck(userString):
    newString = userString.lower()
    reversedString = newString[::-1]
    
    if newString == reversedString:
        print(f"{userString} is a Palindrome!")
    else:
        print(f"{userString} is not a Palindrome!")
        
paliDCheck(userString)