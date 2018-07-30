package cn.kkl.mall.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.kkl.mall.cart.service.CartService;
import cn.kkl.mall.order.pojo.OrderInfo;
import cn.kkl.mall.order.service.OrderSerivce;
import cn.kkl.mall.pojo.E3Result;
import cn.kkl.mall.pojo.TbItem;
import cn.kkl.mall.pojo.TbUser;
import cn.kkl.mall.service.JedisClient;

@Controller
public class OrderController {
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderSerivce orderSerivce;
	
	@Autowired
	private JedisClient jedisClient;
	
	@RequestMapping(value="/order/order-cart")
	public String showOrderCart(HttpServletRequest request) {
		TbUser user=(TbUser) request.getAttribute("user");
		List<TbItem> cartList = cartService.getCartList(user.getId());
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}
	
	/**
	 * create order logic:
	 * 1. get user information from request
	 * 2. add user information to parameter orderInfo
	 * 3. if order create successful , delete cart data in redis
	 * 4. transfer orderId to display page
	 * @param request
	 * @param orderInfo
	 * @return
	 */
	@RequestMapping(value="/order/create",method=RequestMethod.POST)
	public String createOrder(HttpServletRequest request,OrderInfo orderInfo) {
		TbUser user=(TbUser) request.getAttribute("user");
		orderInfo.setBuyerNick(user.getUsername());
		orderInfo.setUserId(user.getId());
		E3Result e3Result = orderSerivce.createOrder(orderInfo);
		if (e3Result.getStatus()==200) {
			String orderId=(String) e3Result.getData();
			request.setAttribute("orderId", orderId);
			
		}
		return "success";
	}

	
}
