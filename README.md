# Minesweeper
A functional minesweeper engine where you can change the number of rows, columns, and mines.
![Tool Image](https://i.imgur.com/fWJXki3.png)

## How to use
The tool initially generates an int[] containing every integer from 1 to n (n depends on the # of elements, set to the 6th tick by default), and randomize the order of its elements. The green rectangles above the input panel is the visual representation of this array. The user can edit the # of elements, as well as the speed of the sort, then click on one of the six sorting algorithms to begin the sort. 

Once the sort begins, only the speed can be changed until the sort ends. To stop the sort for any reason, the user can click on "Force Stop". 

At any given time during the sort, two rectangles within the visual representation will be red. These rectangles represent the two elements being compared at that given time.
