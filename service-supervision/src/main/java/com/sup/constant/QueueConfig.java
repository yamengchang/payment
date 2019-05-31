package com.sup.constant;

import com.sup.pojo.*;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

/**
 * @author tdf
 * @date 2019-01-07 11:47
 **/
@Component
public class QueueConfig {
	public final static String account = Accounting.class.getSimpleName();
	public final static String lease = Lease.class.getSimpleName();
	public final static String house = House.class.getSimpleName();

	@Bean
	public Queue accountUpdateQueue() {
		return new Queue(account + "-update");
	}

	@Bean
	public Queue accountSaveQueue() { 	return new Queue(account + "-save"); 	}

	@Bean
	public Queue accountbatchQueue() { 	return new Queue(account + "-batchsave"); 	}

	@Bean
	public Queue leaseUpdateQueue() {
		return new Queue(lease + "-update");
	}

	@Bean
	public Queue leaseSaveQueue() { 	return new Queue(lease + "-save"); 	}

	@Bean
	public Queue leasebatchQueue() { 	return new Queue(lease + "-batchsave"); 	}



 	@Bean
	public Queue houseUpdateQueue() {
		return new Queue(house + "-update");
	}

	@Bean
	public Queue houseSaveQueue() { 	return new Queue(house + "-save"); 	}

	@Bean
	public Queue housebatchQueue() { 	return new Queue(house + "-batchsave"); 	}




	@Bean
	public Binding bindingExchangePermUserRegister(Queue housebatchQueue, FanoutExchange fanoutExchange) {
		return BindingBuilder.bind(housebatchQueue).to(fanoutExchange);
	}
}
