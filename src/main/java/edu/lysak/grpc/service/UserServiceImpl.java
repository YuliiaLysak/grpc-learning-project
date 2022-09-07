package edu.lysak.grpc.service;

import edu.lysak.grpc.client.OrderClient;
import edu.lysak.grpc.db.User;
import edu.lysak.grpc.db.UserDao;
import edu.lysak.grpc.stubs.order.Order;
import edu.lysak.grpc.stubs.user.Gender;
import edu.lysak.grpc.stubs.user.UserRequest;
import edu.lysak.grpc.stubs.user.UserResponse;
import edu.lysak.grpc.stubs.user.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    private final UserDao userDao = new UserDao();

    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        User user = userDao.getDetails(request.getUsername());

        List<Order> orders = getOrders(user.getId());
        UserResponse userResponse = UserResponse.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setName(user.getName())
                .setAge(user.getAge())
                .setGender(Gender.valueOf(user.getGender()))
                .setNoOfOrders(orders.size())
                .build();

        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

    private List<Order> getOrders(int userId) {
        //get orders by invoking the Order Client
        logger.info("Creating a channel and calling the Order Client");
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50052")
                .usePlaintext()
                .build();
        OrderClient orderClient = new OrderClient(channel);
        List<Order> orders = orderClient.getOrders(userId);

        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            logger.log(Level.SEVERE, "Channel did not shutdown", exception);
        }
        return orders;
    }
}
