package hcmute.it.furnitureshop.Service;

import hcmute.it.furnitureshop.DTO.*;
import hcmute.it.furnitureshop.Entity.Order;
import hcmute.it.furnitureshop.Entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    public <S extends Order> void save(User user, OrderRequestDTO orderRequestDTO, Integer productId);
    public String CancelOrder(Integer orderId);
    public String RestoreOrder(Integer orderId);
    public Iterable<Order> findByUser(User user);
    public Optional<Order> findById(Integer orderId);
    public Iterable<Order> findOrderByUserAndState(String username,String state);
    List<OrderDTO> getAllOrder();
    String deleteOrder(Integer orderId);
    OrderDTO getById(Integer orderId);
    String UpdateOrderState(Integer orderId);
    int totalOrderInMonth(int month);
    long totalRevenueOrderInMonth(int month);
    ArrayList<DataChartDTO> getDataChart(int month);
    int totalOrderInYear(int year);
    long totalRevenueOrderInYear(int year);
    ArrayList<DataChartDTO> getDataChartInYear(int year);
    ArrayList<OrderDashboardDTO> get10RecentOrder();
    ArrayList<RevenueRoom> getListRevenueRoomInMonth(int month);
    ArrayList<RevenueRoom> getListRevenueRoomInYear(int year);
}
