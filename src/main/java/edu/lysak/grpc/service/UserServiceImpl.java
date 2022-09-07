package edu.lysak.grpc.service;

import edu.lysak.grpc.db.User;
import edu.lysak.grpc.db.UserDao;
import edu.lysak.shopping.stubs.user.Gender;
import edu.lysak.shopping.stubs.user.UserRequest;
import edu.lysak.shopping.stubs.user.UserResponse;
import edu.lysak.shopping.stubs.user.UserServiceGrpc;
import io.grpc.stub.StreamObserver;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        UserDao userDao = new UserDao();
        User user = userDao.getDetails(request.getUsername());

        UserResponse userResponse = UserResponse.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setName(user.getName())
                .setAge(user.getAge())
                .setGender(Gender.valueOf(user.getGender()))
                .build();

        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }
}
