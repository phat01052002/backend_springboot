package hcmute.it.furnitureshop.Service.Impl;

import hcmute.it.furnitureshop.Common.RankEnum;
import hcmute.it.furnitureshop.DTO.*;
import hcmute.it.furnitureshop.Entity.Notification;
import hcmute.it.furnitureshop.Entity.Order;
import hcmute.it.furnitureshop.Entity.Product;
import hcmute.it.furnitureshop.Entity.User;
import hcmute.it.furnitureshop.Repository.NotificationRepository;
import hcmute.it.furnitureshop.Repository.OrderRepository;
import hcmute.it.furnitureshop.Repository.ProductRepository;
import hcmute.it.furnitureshop.Repository.UserRepository;
import hcmute.it.furnitureshop.Service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public <S extends Order> void save(User user, OrderRequestDTO orderRequestDTO,Integer productId) {
        Order order=new Order();
        Optional<Product> product=productRepository.findById(productId);
        order.setUser(user);
        order.setProduct(product.get());
        order.setState("processing");
        order.setDate(new Date());
        order.setDateUpdate(new Date());
        order.setCount(orderRequestDTO.getCount());
        order.setPaid(orderRequestDTO.getPaid());
        order.setNowDelivery(orderRequestDTO.getNowDelivery());
        order.setPrice(orderRequestDTO.getPrice());
        ///
        Notification notification=new Notification();
        notification.setState(false);
        notification.setDescription("Đặt hàng thành công");
        notification.setUser(user);
        notification.setDate(new Date());
        List<Notification> notifications=new ArrayList<>();
        notifications.add(notification);
        //// trừ số lượng khi đặt hàng
        product.get().setQuantity(product.get().getQuantity()-orderRequestDTO.getCount());
        productRepository.save(product.get());
        //
        order.setNotification(notifications);
        orderRepository.save(order);
        notification.setOrder(order);
        notificationRepository.save(notification);
    }

    @Override
    public String CancelOrder(Integer orderId) {
        Optional<Order> order=orderRepository.findById(orderId);
        if(order.isPresent()){
            if(order.get().getState().equals("processing")) {
                Product product = order.get().getProduct();
                if(product != null){
                    order.get().setState("canceled");
                    Notification notification = new Notification();
                    notification.setState(false);
                    notification.setDescription("Huỷ đơn hàng thành công");
                    notification.setUser(order.get().getUser());
                    notification.setDate(new Date());
                    notification.setOrder(order.get());
                    notificationRepository.save(notification);
                    orderRepository.save(order.get());
                    product.setQuantity(product.getQuantity() + order.get().getCount());
                    productRepository.save(product);
                    return "canceled success";
                }else {
                    return "canceled fail";
                }
            }else {
                return "this order can't canceled";
            }
        }else{
            return "canceled fail";
        }
    }

    @Override
    public String RestoreOrder(Integer orderId) {
        Optional<Order> order=orderRepository.findById(orderId);
        if(order.isPresent()){
            if(order.get().getState().equals("canceled")){
                Product product= order.get().getProduct();
                if(product.getQuantity()>0){
                    order.get().setState("processing");
                    order.get().setDate(new Date());
                    orderRepository.save(order.get());

                    Notification notification=new Notification();
                    notification.setState(false);
                    notification.setDescription("Đặt lại đơn hàng thành công");
                    notification.setUser(order.get().getUser());
                    notification.setDate(new Date());
                    notification.setOrder(order.get());

                    notificationRepository.save(notification);

                    product.setQuantity(product.getQuantity()-order.get().getCount());
                    productRepository.save(product);
                    return "restore success";
                }else{
                    return "not enough product quantity";
                }
            }else{
                return "this order can't restore";
            }
        }else{
            return "restore fail";
        }
    }

    @Override
    public Iterable<Order> findByUser(User user) {
        return orderRepository.findOrdersByUserOrderByDateDesc(user);
    }

    @Override
    public Optional<Order> findById(Integer orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public Iterable<Order> findOrderByUserAndState(String username,String state) {
        Optional<User> user=userRepository.findByUsername(username);
        if(user.isPresent()) {
            Iterable<Order> orders = orderRepository.findOrdersByUserOrderByDateDesc(user.get());
            ArrayList<Order> ordersReturn = new ArrayList<>();
            orders.forEach(order -> {
                if (order.getState().equals(state)) {
                    ordersReturn.add(order);
                }
            });
            return ordersReturn;
        }
        else{
            return new ArrayList<>();
        }
    }

    @Override
    public List<OrderDTO> getAllOrder() {
        List<OrderDTO> orderDTOS= new ArrayList<>();
        orderRepository.findAll().forEach(order -> {
            var orderDTO = OrderDTO.builder().orderId(order.getOrderId())
                    .count(order.getCount())
                    .date(order.getDate())
                    .state(order.getState())
                    .paid(order.getPaid())
                    .dateUpdate(order.getDateUpdate())
                    .nowDelivery(order.getNowDelivery())
                    .productId(order.getProduct().getProductId())
                    .productName(order.getProduct().getName())
                    .productPrice(order.getProduct().getPrice())
                    .userName(order.getUser().getName())
                    .total((int) (order.getCount()* order.getProduct().getPrice()))
                    .build();
            if(order.getProduct().getImageProducts().size() > 0)
                orderDTO.setImage1(order.getProduct().getImageProducts().get(0).getImage());
            orderDTOS.add(orderDTO);
        });
        return orderDTOS;
    }

    @Override
    public String deleteOrder(Integer orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if(order.isPresent())
        {
            orderRepository.deleteById(orderId);
            return "Xóa đơn hàng thành công";
        }
        return "Xóa đơn hàng thất bại";
    }

    @Override
    public OrderDTO getById(Integer orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if(order.isPresent()){
            OrderDTO orderDTO = OrderDTO.builder().orderId(order.get().getOrderId())
                    .count(order.get().getCount())
                    .date(order.get().getDate())
                    .state(order.get().getState())
                    .paid(order.get().getPaid())
                    .dateUpdate(order.get().getDateUpdate())
                    .nowDelivery(order.get().getNowDelivery())
                    .productId(order.get().getProduct().getProductId())
                    .productName(order.get().getProduct().getName())
                    .productPrice(order.get().getProduct().getPrice())
                    .userName(order.get().getUser().getName())
                    .total((int) (order.get().getCount()* order.get().getProduct().getPrice()))
                    .build();
            if(order.get().getProduct().getImageProducts().size() >0)
                orderDTO.setImage1(order.get().getProduct().getImageProducts().get(0).getImage());
            return orderDTO;
        }
        return null;
    }

    @Override
    public String UpdateOrderState(Integer orderId) {
        Optional<Order> order=orderRepository.findById(orderId);
        String message = null;
        if (order.isPresent() && !order.get().getState().isEmpty())
        {
            switch (order.get().getState()) {
                case "processing" -> {
                    order.get().setState("processed");
                    message = "Đơn hàng đã được xác nhận";
                }
                case "processed" -> {
                    order.get().setState("delivered");
                    message = "Đơn hàng đã được giao";
                    order.get().setPaid(true);
                }
            }
            if (order.get().getPaid())
                pointCalculate(order.get().getUser().getUserId(), orderId);
            /// Tạo thông báo
            Notification notification=new Notification();
            notification.setState(false);
            notification.setDescription(message);
            notification.setUser(order.get().getUser());
            notification.setDate(new Date());
            notification.setOrder(order.get());
            notificationRepository.save(notification);
            return "Chuyển trạng thái thành công";
        }
        else return "Chuyển trạng thái thất bại";
    }

    private void pointCalculate(Integer userId, Integer orderId)
    {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent())
        {
            Optional<Order> order = orderRepository.findById(orderId);
            int point = (int)(user.get().getPoint() + order.get().getCount()*(order.get().getProduct().getPrice())/100000);
            user.get().setPoint(point);
            if (point >0 && point <= 100)
                user.get().setRankUser(RankEnum.BRONZE);
            else if (point<=300)
                user.get().setRankUser(RankEnum.SILVER);
            else if (point<=500)
                user.get().setRankUser(RankEnum.GOLD);
            else if (point<=700)
                user.get().setRankUser(RankEnum.PLATINUM);
            else
                user.get().setRankUser(RankEnum.DIAMOND);
        }
    }

    @Override
    public int totalOrderInMonth(int month) {
        AtomicInteger totalOrder = new AtomicInteger();
        orderRepository.findAll().forEach(order -> {
            if(!order.getState().equals("canceled"))
                if(order.getDateUpdate().getMonth() == month-1)
                    totalOrder.getAndIncrement();
        });
        return totalOrder.get();
    }
    @Override
    public long totalRevenueOrderInMonth(int month) {
        AtomicLong totalOrder = new AtomicLong();
        orderRepository.findAll().forEach(order -> {
            if(order.getState().equals("delivered")) {
                if (order.getDateUpdate().getMonth() == month-1)
                    totalOrder.set(totalOrder.get() + order.getPrice());
            }
        });
        return totalOrder.get();
    }

    @Override
    public ArrayList<DataChartDTO> getDataChart(int month) {
        ArrayList<DataChartDTO> dataChartDTOS = new ArrayList<>();
        orderRepository.findAll().forEach(order -> {
            if(order.getDateUpdate().getMonth() == month-1 && order.getState().equals("delivered"))
                dataChartDTOS.add(
                        DataChartDTO
                                .builder()
                                .productId(order.getProduct().getProductId())
                                .revenue(order.getPrice())
                                .productName(order.getProduct().getName())
                                .build()
            );
        });
        Collections.sort(dataChartDTOS);
        ArrayList<DataChartDTO> result = new ArrayList<>();
        for(int i = 0; i<dataChartDTOS.size()-1; i++)
        {
            if(dataChartDTOS.get(i).getProductId().equals(dataChartDTOS.get(i+1).getProductId()))
            {
                dataChartDTOS.set(i,
                        new DataChartDTO(
                                dataChartDTOS.get(i).getProductId()
                                , dataChartDTOS.get(i).getRevenue()+dataChartDTOS.get(i+1).getRevenue()
                                , dataChartDTOS.get(i).getProductName()
                        )
                );
                dataChartDTOS.remove(i+1);
            }
        }
        for(int i = 0; i<dataChartDTOS.size() && i<5 ; i++)
        {
            result.add(dataChartDTOS.get(i));
        }
        return (result);
    }

    @Override
    public int totalOrderInYear(int year) {
        AtomicInteger totalOrder = new AtomicInteger();
        orderRepository.findAll().forEach(order -> {
            if(!order.getState().equals("canceled"))
                if(order.getDateUpdate().getYear()+1900 == year)
                    totalOrder.getAndIncrement();
        });
        return totalOrder.get();
    }
    @Override
    public long totalRevenueOrderInYear(int year) {
        AtomicLong totalOrder = new AtomicLong();
        orderRepository.findAll().forEach(order -> {
            if(order.getState().equals("delivered")) {
                if (order.getDateUpdate().getYear()+1900 == year)
                    totalOrder.set(totalOrder.get() + order.getPrice());
            }
        });
        return totalOrder.get();
    }

    @Override
    public ArrayList<DataChartDTO> getDataChartInYear(int year) {
        ArrayList<DataChartDTO> dataChartDTOS = new ArrayList<>();
        orderRepository.findAll().forEach(order -> {
            if(order.getDateUpdate().getYear()+1900 == year && order.getState().equals("delivered"))
                dataChartDTOS.add(
                        DataChartDTO
                                .builder()
                                .productId(order.getProduct().getProductId())
                                .revenue(order.getPrice())
                                .productName(order.getProduct().getName())
                                .build()
                );
        });
        Collections.sort(dataChartDTOS);
        ArrayList<DataChartDTO> result = new ArrayList<>();
        for(int i = 0; i<dataChartDTOS.size()-1; i++)
        {
            if(dataChartDTOS.get(i).getProductId().equals(dataChartDTOS.get(i+1).getProductId()))
            {
                dataChartDTOS.set(i,
                        new DataChartDTO(
                                dataChartDTOS.get(i).getProductId()
                                , dataChartDTOS.get(i).getRevenue()+dataChartDTOS.get(i+1).getRevenue()
                                , dataChartDTOS.get(i).getProductName()
                        )
                );
                dataChartDTOS.remove(i+1);
            }
        }
        for(int i = 0; i<dataChartDTOS.size() && i<5 ; i++)
        {
            result.add(dataChartDTOS.get(i));
        }
        return (result);
    }

    @Override
    public ArrayList<OrderDashboardDTO> get10RecentOrder() {
        ArrayList<OrderDashboardDTO> orderDTOS = new ArrayList<>();
        orderRepository.findAll().forEach(order -> {

            orderDTOS.add(OrderDashboardDTO.builder().userName(order.getUser().getName())
                    .dateUpdate(order.getDateUpdate())
                    .orderId(order.getOrderId())
                    .total(order.getPrice()).build());

        });
        Collections.sort(orderDTOS);
        ArrayList<OrderDashboardDTO> result = new ArrayList<>();
        for(int i = 0; i<orderDTOS.size() && i < 10; i++)
        {
            result.add(orderDTOS.get(i));
        }
        return (result);
    }

    @Override
    public ArrayList<RevenueRoom> getListRevenueRoomInMonth(int month) {
        long totalRevenue = 0;
        ArrayList<Order> orders = (ArrayList<Order>) orderRepository.findAll();
        for(int i = 0; i< orders.size(); i++)
        {
            if(orders.get(i).getDateUpdate().getMonth() == month-1 && orders.get(i).getState().equals("delivered"))
                totalRevenue = totalRevenue + orders.get(i).getPrice();
        }
        long livingRoomRevenue = 0, bedRoomRevenue = 0, workRoomRevenue = 0, kitchenRenvenue = 0;
        for(int i = 0; i< orders.size(); i++)
        {
            if(orders.get(i).getDateUpdate().getMonth() == month-1 && orders.get(i).getState().equals("delivered")) {
                if (orders.get(i).getProduct().getCategory().getRoom().getRoomName().equals("Phòng Khách")) {
                    livingRoomRevenue = livingRoomRevenue + orders.get(i).getPrice();
                }
                if (orders.get(i).getProduct().getCategory().getRoom().getRoomName().equals("Phòng Ngủ")) {
                    bedRoomRevenue = bedRoomRevenue + orders.get(i).getPrice();
                }
                if (orders.get(i).getProduct().getCategory().getRoom().getRoomName().equals("Phòng Làm Việc")) {
                    workRoomRevenue = workRoomRevenue + orders.get(i).getPrice();
                }
                if (orders.get(i).getProduct().getCategory().getRoom().getRoomName().equals("Phòng Bếp")) {
                    kitchenRenvenue = kitchenRenvenue + orders.get(i).getPrice();
                }
            }
        }
            ArrayList<RevenueRoom> revenueRooms = new ArrayList<>();
            revenueRooms.add(new RevenueRoom("Phòng Khách", (double) (livingRoomRevenue)));
            revenueRooms.add(new RevenueRoom("Phòng Ngủ", (double) (bedRoomRevenue)));
            revenueRooms.add(new RevenueRoom("Phòng Làm Việc", (double) (workRoomRevenue)));
            revenueRooms.add(new RevenueRoom("Phòng Bếp", (double) (kitchenRenvenue)));
            Collections.sort(revenueRooms);
            return revenueRooms;
    }

    @Override
    public ArrayList<RevenueRoom> getListRevenueRoomInYear(int year) {
        long totalRevenue = 0;
        ArrayList<Order> orders = (ArrayList<Order>) orderRepository.findAll();
        for(int i = 0; i< orders.size(); i++)
        {
            if(orders.get(i).getDateUpdate().getYear()+1900 == year && orders.get(i).getState().equals("delivered"))
                totalRevenue = totalRevenue + orders.get(i).getPrice();
        }
        long livingRoomRevenue = 0, bedRoomRevenue = 0, workRoomRevenue = 0, kitchenRenvenue = 0;
        for(int i = 0; i< orders.size(); i++)
        {
            if(orders.get(i).getDateUpdate().getYear()+1900 == year && orders.get(i).getState().equals("delivered")) {
                if (orders.get(i).getProduct().getCategory().getRoom().getRoomName().equals("Phòng Khách")) {
                    livingRoomRevenue = livingRoomRevenue + orders.get(i).getPrice();
                }
                if (orders.get(i).getProduct().getCategory().getRoom().getRoomName().equals("Phòng Ngủ")) {
                    bedRoomRevenue = bedRoomRevenue + orders.get(i).getPrice();
                }
                if (orders.get(i).getProduct().getCategory().getRoom().getRoomName().equals("Phòng Làm Việc")) {
                    workRoomRevenue = workRoomRevenue + orders.get(i).getPrice();
                }
                if (orders.get(i).getProduct().getCategory().getRoom().getRoomName().equals("Phòng Bếp")) {
                    kitchenRenvenue = kitchenRenvenue + orders.get(i).getPrice();
                }
            }
        }
            ArrayList<RevenueRoom> revenueRooms = new ArrayList<>();
            revenueRooms.add(new RevenueRoom("Phòng Khách", (double) (livingRoomRevenue)));
            revenueRooms.add(new RevenueRoom("Phòng Ngủ", (double) (bedRoomRevenue)));
            revenueRooms.add(new RevenueRoom("Phòng Làm Việc", (double) (workRoomRevenue)));
            revenueRooms.add(new RevenueRoom("Phòng Bếp", (double) (kitchenRenvenue)));
            return revenueRooms;
    }
}
