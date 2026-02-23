package praccticeSessions.exercises;
class Shape {

    public double getArea(){
        System.out.println("Area not defined for shape.");
        return 0;
    }
}
class Rectangle extends Shape{
    double length;
    double width;


    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    public double getArea(){
        return length * width;
    }
}
public class n{
    public static void main(String[] args) {
        Rectangle rect = new Rectangle(10.5, 5.0);

        System.out.println("Area of Rectangle: " + rect.getArea());
    }
}