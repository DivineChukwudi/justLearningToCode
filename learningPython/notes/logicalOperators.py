# (and, or, not) used to check if 2 or more conditional statements are true

temp = int(input("What is the temperature outside?(in °C): "))


#Using 'and' logical operator
#if temp < 0:
#    print("Jesus Christ! It's freezing out there!")

#if temp >= 0  and temp <= 15:
    #print("It's cold outside")
#elif temp > 15  and temp <=25:
    #print("Oh what a lovely day init?")
#else:
#   print("HOT!!! HOT!! HOT!! GET INSIDE OL'CHAP!")
    
#Using 'or' logical operator
#elif temp < 0 or temp > 25:
  #  print("Extreme weather condition mate, you don't want to go out there!")
    
    
    
#Using 'not' logical operator
#if not(temp < 0 or temp > 25):
#    print("Lovely day mate, go out and have some fun!")
#elif not(temp >= 0  and temp <= 15):
 #   print("beautiful day mate, go out and have some fun!")
#elif not(temp > 15  and temp <=25):
 #   print("cold day mate, you might want to wear a jacket")


#fix
if not (temp < 0 or temp > 25):
    print("Lovely day mate, go out and have some fun!")
elif not (temp >= 0):
    print("Cold day mate, you might want to wear a jacket")
else:
    print("It's hot outside, stay hydrated!")
    
    
#final not: not is to b used when it simplifies the code otherwise avoid using it.