package edu.lysak.grpc.service;

import com.google.protobuf.util.Timestamps;
import edu.lysak.grpc.db.OrderDao;
import edu.lysak.grpc.db.OrderFromDb;
import edu.lysak.grpc.stubs.order.Order;
import edu.lysak.grpc.stubs.order.OrderRequest;
import edu.lysak.grpc.stubs.order.OrderResponse;
import edu.lysak.grpc.stubs.order.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    private final Logger logger = Logger.getLogger(OrderServiceImpl.class.getName());
    private final OrderDao orderDao = new OrderDao();

    @Override
    public void getOrdersForUser(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        List<OrderFromDb> orders = orderDao.getOrders(request.getUserId());
        logger.info("Got orders from OrderDao and converting to OrderResponse proto objects");
        List<Order> ordersForUser = orders.stream()
                .map(order -> Order.newBuilder()
                        .setUserId(order.getUserId())
                        .setOrderId(order.getOrderId())
                        .setNoOfItems(order.getNoOfItems())
                        .setTotalAmount(order.getTotalAmount())
                        .setOrderDate(Timestamps.fromMillis(order.getOrderDate().getTime()))
                        .build()
                )
                .collect(Collectors.toList());
        OrderResponse orderResponse = OrderResponse.newBuilder()
                .addAllOrder(ordersForUser)
                .build();
        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();
    }
}
