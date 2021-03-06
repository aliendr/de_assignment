#   Differential Equation Graph Plotting Application

This app shows how different numeric methods (Euler, Improved Euler, Runge-Kutta) can be used to solve a first-order DE` y'= -2y + 4x` and also plots graph of its analytical solution to compare results and show effectiveness of each method.
***
Application was written on Java, using JavaFx libraries to construct GUI, where you have an ability to change end of the interval, step between x values and maximum number of steps to calculate the global error.
***

## Initial Value Problem

```angular2html
y` = -2y + 4x
y(0) = 0 
x belongs to [0;X]
```
Solved analytically
***
![image](init_value.png)

## Numerical method solutions
After studying what Euler, Improved Euler, Runge-Kutta methods are, it was not difficult to implement functions computing them on Java.

For every method there writen functions which are updating when user changes the data.

***
## My function at given initial values:
![image](graphs.jpg)

## All methods give almost the same result, but when we compare them the difference is more visible:
![image](eeeror.jpg)

## As expected, on the global error graph we see how big is inaccuracy of Euler`s method.
![image](global_errors.jpg)
However, in this particular example the global error is decreasing as we move to the right. I tried a lot different initial values to recalculate and the result was quiet same. Firstly, it put me in bewilderment, but then looked closely and understood that the bottom axe is the number of steps and as it increasing - increasing the accuracy.
>
***
#### *What i learned*
# Never and never dedlainitь anymore
