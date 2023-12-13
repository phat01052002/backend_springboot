package hcmute.it.furnitureshop.Controller;

import hcmute.it.furnitureshop.DTO.*;
import hcmute.it.furnitureshop.Config.JwtService;
import hcmute.it.furnitureshop.Entity.*;
import hcmute.it.furnitureshop.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@CrossOrigin( origins = "*" , allowedHeaders = "*")
public class AdminController {
    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderService orderService;
    @Autowired
    DiscountService discountService;
    @Autowired
    RoomService roomService;
    @Autowired
    BannerService bannerService;
    @Autowired
    JwtService jwtService;
    public String getToken(){
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest()
                .getHeader("Authorization")
                .replace("Bearer ","");
    }
    @RequestMapping("/findByName")
    public Optional<User> findByName(){
        return userService.findByName(jwtService.extractUserName(getToken()));

    }
    @RequestMapping("/check")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello Admin");
    }

    //CRUD User
    @RequestMapping("/getUsers")
    public List<User> getAll(){
        return userService.getAll();
    }

    @RequestMapping("/getUserById/{userId}")
    public ResponseDTO<UserDTO> getUserById(@PathVariable("userId") Integer userId){
        UserDTO userDTO = userService.getById(userId);
        if(userDTO != null){
            return new ResponseDTO<>(userDTO, "Ok", "Lấy thông tin người dùng thành công");
        }
        else{
            return new ResponseDTO<>(null, "Fail", "Không tồn tại người dùng");
        }
    }

    @PostMapping("/createUser")
    public ResponseDTO<?> createUser(@RequestBody CreateUserDTO createUserDTO){
        User user = userService.createUser(createUserDTO);
        if(user != null){
            return new ResponseDTO<>(userService.createUser(createUserDTO), "Ok", "Thêm người dùng thành công");
        }
        else{
            return new ResponseDTO<>(null, "Fail", "Thêm người dùng thất bại ! Đã tồn tại user trong hệ thống");
        }
    }

    @PostMapping("/updateUserStatus/{userId}")
    public ResponseDTO<?> updateUser(@PathVariable("userId") Integer userId){
        String message = userService.updateStatusUser(userId);
        return new ResponseDTO<>(null, "Ok", message);
    }

    @RequestMapping("/deleteUser/{userId}")
    public ResponseDTO<?> deleteUser(@PathVariable("userId") Integer userId){
        String message = userService.deleteUser(userId);
        return new ResponseDTO<>(null, "Ok", message);
    }
    //CRUD Product
    @RequestMapping("/products")
    public Iterable<ProductDetailDTO> getAllProduct(){
        return productService.getAllProductsWithCategoryName();
    }

    @RequestMapping("/getProductById/{productId}")
    public ResponseDTO<ProductDetailDTO> getProductById(@PathVariable("productId") Integer productId){
        ProductDetailDTO productDetailDTO = productService.getById(productId);
        if(productDetailDTO != null){
            return new ResponseDTO<>(productDetailDTO, "Ok", "Lấy thông tin sản phẩm thành công");
        }
        else{
            return new ResponseDTO<>(null, "Fail", "Không tồn tại sản phẩm");
        }
    }

    @PostMapping("/createProduct")
    public ResponseDTO<?> createProduct(@RequestBody ProductDetailDTO createProductDTO){
        Product product = productService.createProduct(createProductDTO);
        if(product != null){
            return new ResponseDTO<>(product, "Ok", "Thêm sản phẩm thành công");
        }
        else{
            return new ResponseDTO<>(null, "Fail", "Thêm sản phẩm thất bại ! Đã tồn tại sản phẩm trong hệ thống");
        }
    }

    @PostMapping("/updateProduct")
    public ResponseDTO<?> updateProduct(@RequestBody ProductDetailDTO productDTO){
        String message = productService.updateProduct(productDTO);
        return new ResponseDTO<>(null, "Ok", message);
    }

    @RequestMapping("/deleteProduct/{productId}")
    public ResponseDTO<?> deleteProduct(@PathVariable("productId") Integer productId){
        String message = productService.deleteProduct(productId);
        return new ResponseDTO<>(null, "Ok", message);
    }

    ////////////////////////Order
    @RequestMapping("/categories")
    public List<CategoryDTO> getAllCategory(){
        return categoryService.getListCate();
    }

    @RequestMapping("/getCategoryById/{cateId}")
    public ResponseDTO<CategoryDTO> getCategoryById(@PathVariable("cateId") Integer cateId){
        CategoryDTO categoryDTO = categoryService.getById(cateId);
        if(categoryDTO != null){
            return new ResponseDTO<>(categoryDTO, "Ok", "Lấy thông tin loại sản phẩm thành công");
        }
        else{
            return new ResponseDTO<>(null, "Ok", "Không tồn tại loại sản phẩm");
        }
    }
    @PostMapping("/createCategory")
    public ResponseDTO<?> createCategory(@RequestBody CategoryDTO categoryDTO){
        Category category = categoryService.createCategory(categoryDTO);
        if(category != null){
            return new ResponseDTO<>(category, "Ok", "Thêm loại sản phẩm thành công");
        }
        else{
            return new ResponseDTO<>(null, "Fail", "Thêm loại sản phẩm thất bại ! Đã tồn tại sản phẩm trong hệ thống");
        }
    }

    @PostMapping("/updateCategory")
    public ResponseDTO<?> updateCategory(@RequestBody CategoryDTO categoryDTO){
        String message = categoryService.updateCategory(categoryDTO);
        return new ResponseDTO<>(null, "Ok", message);
    }

    @RequestMapping("/deleteCategory/{cateId}")
    public ResponseDTO<?> deleteCategory(@PathVariable("cateId") Integer cateId){
        String message = categoryService.deleteCategory(cateId);
        return new ResponseDTO<>(null, "Ok", message);
    }

    @RequestMapping("/getCategoryList")
    public List<Object> getCategoryList()
    {
        List<Object> cateList = new ArrayList<>();
        categoryService.getAll().forEach(category -> {
            cateList.add(category.getName());
        });
        return cateList;
    }
    //////Oder
    @RequestMapping("/orders")
    public List<OrderDTO> getAllOrder(){
        return orderService.getAllOrder();
    }
    @RequestMapping("/deleteOrder/{orderId}")
    public ResponseDTO<?> deleteOrder(@PathVariable("orderId") Integer orderId){
        String message = orderService.deleteOrder(orderId);
        return new ResponseDTO<>(null, "Ok", message);
    }
    @RequestMapping("/getOrderById/{orderId}")
    public ResponseDTO<OrderDTO> getOrderById(@PathVariable("orderId") Integer orderId){
        OrderDTO orderDTO = orderService.getById(orderId);
        if(orderDTO != null){
            return new ResponseDTO<>(orderDTO, "Ok", "Lấy thông tin đơn hàng thành công");
        }
        else{
            return new ResponseDTO<>(null, "Fail", "Không tồn tại đơn hàng");
        }
    }
    @PostMapping("/updateOrderState/{orderId}")
    public ResponseDTO<OrderDTO> updateOrderState(@PathVariable("orderId") Integer orderId){
        String message = orderService.UpdateOrderState(orderId);
        if(message != null){
            return new ResponseDTO<>(null, "Ok", message);
        }
        else{
            return new ResponseDTO<>(null, "Fail", "Không tồn tại đơn hàng");
        }
    }
    /////////////Room
    @RequestMapping("/getRoomList")
    public List<Object> getRoomList()
    {
        List<Object> roomList = new ArrayList<>();
        roomService.getAll().forEach(room -> {
            roomList.add(room.getRoomName());
        });
        return roomList;
    }
    /////////////Discount
    @RequestMapping("/discounts")
    public List<DiscountDTO> getListDiscount(){
        return discountService.getListDiscount();
    }
    @PostMapping("/createDiscount")
    public ResponseDTO<?> createDiscount(@RequestBody DiscountDTO discountDTO){
        Discount discount = discountService.createDiscount(discountDTO);
        if(discount != null){
            return new ResponseDTO<>(discount, "Ok", "Thêm giảm giá thành công");
        }
        else{
            return new ResponseDTO<>(null, "Fail", "Thêm giảm giá thất bại ! Đã tồn tại giảm giá trong hệ thống");
        }
    }

    @PostMapping("/updateDiscount")
    public ResponseDTO<?> updateDiscount(@RequestBody DiscountDTO discountDTO){
        String message = discountService.updateDiscount(discountDTO);
        return new ResponseDTO<>(null, "Ok", message);
    }

    @RequestMapping("/getDiscountById/{discountId}")
    public ResponseDTO<?> getDiscountById(@PathVariable("discountId") Integer discountId){
        DiscountDTO discountDTO = discountService.getById(discountId);
        if(discountDTO != null){
            return new ResponseDTO<>(discountDTO, "Ok", "Lấy thông tin đơn hàng thành công");
        }
        else{
            return new ResponseDTO<>(null, "Fail", "Không tồn tại đơn hàng");
        }
    }

    @RequestMapping("/deleteDiscount/{disId}")
    public ResponseDTO<?> deleteDiscount(@PathVariable("disId") Integer disId){
        String message = discountService.deleteDiscount(disId);
        return new ResponseDTO<>(null, "Ok", message);
    }
    @RequestMapping("/getDiscountList")
    public List<Object> getDiscountList()
    {
        List<Object> discountList = new ArrayList<>();
        discountService.getAll().forEach(discount -> {
            discountList.add(discount.getDiscountName());
        });
        return discountList;
    }
    ////////////////////Banner
    @RequestMapping("/banners")
    public ArrayList<BannerDTO> getAllBanners(){
        return bannerService.getAllBanner();
    }

    @RequestMapping("/getBannerById/{bannerId}")
    public ResponseDTO<BannerDTO> getBannerById(@PathVariable("bannerId") Integer bannerId){
        BannerDTO bannerDTO = bannerService.getById(bannerId);
        if(bannerDTO != null){
            return new ResponseDTO<>(bannerDTO, "Ok", "Lấy thông tin banner thành công");
        }
        else{
            return new ResponseDTO<>(null, "Fail", "Không tồn tại banner");
        }
    }
    @PostMapping("/createBanner")
    public ResponseDTO<?> createBanner(@RequestBody BannerDTO bannerDTO){
        Banner banner = bannerService.createBanner(bannerDTO);
        if(banner != null){
            return new ResponseDTO<>(banner, "Ok", "Thêm banner thành công");
        }
        else{
            return new ResponseDTO<>(null, "Fail", "Thêm banner thất bại ! Đã tồn tại banner trong hệ thống");
        }
    }

    @PostMapping("/updateBanner")
    public ResponseDTO<?> updateBanner(@RequestBody BannerDTO bannerDTO){
        String message = bannerService.updateBanner(bannerDTO);
        return new ResponseDTO<>(null, "Ok", message);
    }

    @RequestMapping("/deleteBanner/{bannerId}")
    public ResponseDTO<?> deleteBanner(@PathVariable("bannerId") Integer bannerId){
        String message = bannerService.deleteBanner(bannerId);
        return new ResponseDTO<>(null, "Ok", message);
    }

    ////////////////////Dashboard
    @RequestMapping("/dataCardDashboardInMonth/{month}")
    public ResponseDTO<DataCardDashboard> getDataForCardDashboardInMoth(@PathVariable("month") int month)
    {
        int totalNewUser = userService.getTotalNewUserInMonth(month);
        int totalOrder = orderService.totalOrderInMonth(month);
        long totalRevenue = orderService.totalRevenueOrderInMonth(month);
        DataCardDashboard dataCardDashboard = DataCardDashboard
                .builder().totalNewUser(totalNewUser).totalRevenue(totalRevenue).totalProductSold(totalOrder)
                .build();
        if(dataCardDashboard != null){
            return new ResponseDTO<>(dataCardDashboard, "Ok", "Lấy dữ liệu thành công");
        }
        else{
            return new ResponseDTO<>(dataCardDashboard, "Fail", "Không thành công");
        }
    }
    @RequestMapping("/dataChartInMonth/{month}")
    public ResponseDTO<ArrayList<DataChartDTO>> getDataLineChart(@PathVariable("month") int month)
    {
        ArrayList<DataChartDTO> dataChartDTOS = orderService.getDataChart(month);
        if(dataChartDTOS != null){
            return new ResponseDTO<>(dataChartDTOS, "Ok", "Lấy dữ liệu thành công");
        }
        else{
            return new ResponseDTO<>(dataChartDTOS, "Fail", "Không thành công");
        }
    }
    @RequestMapping("/dataCardDashboardInYear/{year}")
    public ResponseDTO<DataCardDashboard> getDataForCardDashboardInYear(@PathVariable("year") int year)
    {
        int totalNewUser = userService.getTotalNewUserInYear(year);
        int totalOrder = orderService.totalOrderInYear(year);
        long totalRevenue = orderService.totalRevenueOrderInYear(year);
        DataCardDashboard dataCardDashboard = DataCardDashboard
                .builder().totalNewUser(totalNewUser).totalRevenue(totalRevenue).totalProductSold(totalOrder)
                .build();
        if(dataCardDashboard != null){
            return new ResponseDTO<>(dataCardDashboard, "Ok", "Lấy dữ liệu thành công");
        }
        else{
            return new ResponseDTO<>(dataCardDashboard, "Fail", "Không thành công");
        }
    }
    @RequestMapping("/dataChartInYear/{year}")
    public ResponseDTO<ArrayList<DataChartDTO>> getDataLineChartInYear(@PathVariable("year") int year)
    {
        ArrayList<DataChartDTO> dataChartDTOS = orderService.getDataChartInYear(year);
        if(dataChartDTOS != null){
            return new ResponseDTO<>(dataChartDTOS, "Ok", "Lấy dữ liệu thành công");
        }
        else{
            return new ResponseDTO<>(dataChartDTOS, "Fail", "Không thành công");
        }
    }
    @RequestMapping("/10RecentOrder")
    public ResponseDTO<?> get10RecentOrder()
    {
        ArrayList<OrderDashboardDTO> orderDTOs = orderService.get10RecentOrder();
        if(orderDTOs != null){
            return new ResponseDTO<>(orderDTOs, "Ok", "Lấy dữ liệu thành công");
        }
        else{
            return new ResponseDTO<>(null, "Fail", "Thất bại");
        }
    }
    @RequestMapping("/top3BestUser")
    public ResponseDTO<?> top3BestUser()
    {
        ArrayList<BestUser> bestUsers = userService.getTop3User();
        if(bestUsers != null){
            return new ResponseDTO<>(bestUsers, "Ok", "Lấy thông tin thành công");
        }
        else{
            return new ResponseDTO<>(bestUsers, "Fail", "Thất bại");
        }
    }

    @RequestMapping("/revenueOfRoomInMonth/{month}")
    public ResponseDTO<?> listPercentRevenueInMonth(@PathVariable("month") int month)
    {
        ArrayList<RevenueRoom> revenueRooms = orderService.getListRevenueRoomInMonth(month);
        if(revenueRooms != null){
            return new ResponseDTO<>(revenueRooms, "Ok", "Lấy thông tin thành công");
        }
        else{
            return new ResponseDTO<>(revenueRooms, "Ok", "Không có dữ liệu");
        }
    }

    @RequestMapping("/revenueOfRoomInYear/{year}")
    public ResponseDTO<?> listPercentRevenueInYear(@PathVariable("year") int year)
    {
        ArrayList<RevenueRoom> revenueRooms = orderService.getListRevenueRoomInYear(year);
        if(revenueRooms != null){
            return new ResponseDTO<>(revenueRooms, "Ok", "Lấy thông tin thành công");
        }
        else{
            return new ResponseDTO<>(revenueRooms, "Ok", "Không có dữ liệu");
        }
    }

}
