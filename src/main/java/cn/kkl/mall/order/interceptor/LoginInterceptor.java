package cn.kkl.mall.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.kkl.mall.cart.service.CartService;
import cn.kkl.mall.pojo.TbItem;
import cn.kkl.mall.pojo.TbUser;
import cn.kkl.mall.sso.service.TokenService;
import cn.kkl.mall.utils.CookieUtils;
import cn.kkl.mall.utils.JsonUtils;

public class LoginInterceptor implements HandlerInterceptor {
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private CartService cartService;
	
	@Value("${SSO_BASE_URL}")
	private String ssoBaseUrl;

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	/* login interceptor preHandle logic:
	 * 1. get token from cookie
	 * 2. judge token does it exists:
	 * 	  if does not ,the user does not login, redirect to sso login page,after user login success ,jump user request page directly
	 * 	  if exists,need to invoke sso service,get user information dependent on token,then judge the user information does it exists:
	 * 		 if does not exists,user login has been expired,need user login again.
	 * 		 if the user information exists,show user is login status.then we write user information in request
	 * 			judge is there any cart data in the cookie:
	 * 				if there is ,combine data in cookie with redis,based on redis cart data.
	 * 3. release
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String cookieValue = CookieUtils.getCookieValue(request, "token", true);
		if (StringUtils.isBlank(cookieValue)) {
			response.sendRedirect(ssoBaseUrl+"/page/login?redirect="+request.getRequestURL());
			return false;
		}
		TbUser tbUser = tokenService.getUserByToken("user:information:session:"+cookieValue);
		if (tbUser==null) {
			response.sendRedirect(ssoBaseUrl+"/page/login?redirect="+request.getRequestURL());
			return false;
		}
		request.setAttribute("user", tbUser);
		String cookieCartValueJson = CookieUtils.getCookieValue(request, "cart", true);
		if (StringUtils.isBlank(cookieCartValueJson)) {
			return true;
		}
		cartService.mergeCart(tbUser.getId(), JsonUtils.jsonToList(cookieCartValueJson, TbItem.class));
		return true;
	}

}
