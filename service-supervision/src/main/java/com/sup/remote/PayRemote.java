package com.sup.remote;

import org.springframework.cloud.openfeign.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zwj on 2019/1/8.
 */
@FeignClient("service-payment")
@Service
public interface PayRemote {
	@GetMapping("/instance/{id}")
	String instanceInfo(@PathVariable("id") String instanceId);
}
