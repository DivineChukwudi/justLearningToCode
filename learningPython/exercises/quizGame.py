import time

questions = ("How many elements are the periodic table?: ", 
"Which animal lays the largest egg?: ", 
"What is the most abundant gas in Earth's atmosphere?: ", 
"How many bones are in the human body?: ", 
"Which planet in the solar system is the hottest?: ")

options = (("A. 28", "B. 45", "C. 118", "D. 98"), 
("A. CHICKEN", "B. SNAKE", "C. OSTRICH", "D. FISH"), 
("A. NITROGEN", "B. OXYGEN", "C. CARBON DIOXIDE", "D. ARGON"), 
("A. 206", "B. 152", "C. 99", "D. 107"), 
("A. MERCURY", "B. VENUS", "C. NEPTUNE", "D. URANUS"))

answers = ("C", "C", "A", "A", "B")
guesses = []
score = 0
questionNum = 0
question = 1


for x in questions:
    print("---------------------------------------------------")
    print(f"Question {question}.")
    print(x)
    for j in options[questionNum]:
        print(j)
    guess = input("Enter an answer: ").upper()
    question += 1
    guesses.append(guess)

    if guess == answers[questionNum]:
        score += 1
        print("CORRECT!")
    else:
        print("WRONG!")
        print(f"The correct answer was {answers[questionNum]}")
    time.sleep(1)
    questionNum += 1

    print(f"Your total score: {score}")