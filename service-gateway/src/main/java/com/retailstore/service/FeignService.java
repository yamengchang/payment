package com.retailstore.service;

import org.springframework.cloud.openfeign.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@FeignClient(value = "authorizationService")
@Service
public interface FeignService {
	@RequestMapping(value = "/uaa/oauth2/check", method = RequestMethod.GET)
	public Map check(@RequestParam("access_token")String access_token);
}
