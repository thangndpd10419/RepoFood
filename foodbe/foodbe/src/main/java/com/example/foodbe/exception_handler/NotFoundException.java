package com.example.foodbe.exception_handler;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);
        //hàm tạo tạo 1 đối tượng instace runtiem exception
        // va vì đã truyền tham số tới hàm message( supper(message) nên đối tươgnj này
        // supper: từ khóa đeer gọi đén contructor hoặc method lpows cha
        // ở đây supper( đối số): là gọi hàm tạo lớp cha
        //supper.method(): gọi method lơp cha
    }
    public  NotFoundException(String message, Throwable cause){
        super(message,cause);
    }
}
