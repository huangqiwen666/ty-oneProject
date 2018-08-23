package com.dascom.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class myCORSFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException 
    {	
    	HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization,File-Type,Content-Type,content-type");
        response.setHeader("Access-Control-Allow-Credentials","true");
        /*if(!request.getRequestURI().contains("v1."))
        {
        	response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            //response.getWriter().print("Service Unavailable");
        	return;
        	//throw new ServletException("没有这个路径");
        }*/
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {

    }
}
