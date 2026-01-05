#   TUPLE = COLLECTION WHICH IS ORDERED AND UNCHANGEABLE
#           TUPLES ARE USED TO GROUP TOGETHER RELATED DATA


student = ('Divine', 21, 'male')

#print(student.count('Divine'))  # 1
#print(student.index("male"))  # 1

for x in student:
    print(x)

if "Divine" in student:
    print("Divine is here!")
    