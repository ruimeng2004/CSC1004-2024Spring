# use tkinter to create python GUI
import tkinter as tk
# use random to generate food randomly on the  game board
import random


class SnakeGame:
    def __init__(self, master):
        self.master = master
        self.master.title("Snake Game")
        self.master.resizable(False, False)

        # we use canvas firstly draw the game board and set the background color as white
        self.canvas = tk.Canvas(master, width=400, height=400, bg="white")
        self.canvas.pack()

        #  the snake which appears at the position (20,20) and the initial direction is towards right
        self.snake = [(20, 20)]
        self.direction = "Right"
        # food is created on the board using create_food method
        self.food = self.create_food()
        # the initial score is 0
        self.score = 0

        self.draw_snake()
        self.draw_food()

        # the program can receive and handle with keyboard control
        self.master.bind("<KeyPress>", self.change_direction)
        self.move_snake()

    # the draw_snake method is used to draw the initail snake
    def draw_snake(self):
        self.canvas.delete("snake")
        for segment in self.snake:
            x, y = segment
            self.canvas.create_rectangle(x, y, x + 20, y + 20, fill="green", tag="snake")

    # food will appear randomly on the board
    def create_food(self):
        x = random.randint(0, 19) * 20
        y = random.randint(0, 19) * 20
        return x, y

    # food is represent as a rectangle and is in red color
    def draw_food(self):
        x, y = self.food
        self.food_item = self.canvas.create_rectangle(x, y, x + 20, y + 20, fill="red", tag="food")

    # the most important part of the program
    def move_snake(self):
        # this is the head of the snake
        head_x, head_y = self.snake[0]
        # four direction which the snake running on
        if self.direction == "Up":
            new_head = (head_x, head_y - 20)
        elif self.direction == "Down":
            new_head = (head_x, head_y + 20)
        elif self.direction == "Left":
            new_head = (head_x - 20, head_y)
        elif self.direction == "Right":
            new_head = (head_x + 20, head_y)

        self.snake.insert(0, new_head)

        # handle the situation when the snake eats the food , that is the snake head changes and score added, promal food disappear and new random food appear
        if new_head == self.food:
            self.score += 1
            self.canvas.delete(self.food_item)
            self.food = self.create_food()
            self.draw_food()
        else:
            self.snake.pop()

        self.draw_snake()

        # game over when the snake reach the boundries of game board or it reach itself
        if self.check_collision() or not (0 <= new_head[0] < 400 and 0 <= new_head[1] < 400):
            self.game_over()
        # set the dynamic velocity according to the score
        else:
            if self.score >= 14:
                delay  = 100
            elif self.score >= 8:
                delay = 150
            else:
                delay = 200
            self.master.after(delay, self.move_snake)

    # method to check whether the collision has appeared
    def check_collision(self):
        return len(self.snake) != len(set(self.snake))

    # receive the keyboard message and chage direction according to the keyboard message
    def change_direction(self, event):
        if event.keysym in ["Up", "Down", "Left", "Right"]:
            new_direction = event.keysym
            opposite_directions = {"Up": "Down", "Down": "Up", "Left": "Right", "Right": "Left"}
            if new_direction != opposite_directions[self.direction]:
                self.direction = new_direction

    # print out game over and score message when game is over
    def game_over(self):
        self.canvas.create_text(200, 200, text=f"Game Over！\nScore：{self.score}", fill="black", font=("Arial", 20),
                                tag="gameover")


root = tk.Tk()
game = SnakeGame(root)
root.mainloop()
