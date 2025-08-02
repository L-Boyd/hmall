package com.hmall.api.client;

import com.hmall.api.dto.LoginFormDTO;
import com.hmall.api.vo.UserLoginVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {

    @PostMapping("/users/login")
    UserLoginVO login(@RequestBody @Validated LoginFormDTO loginFormDTO);

    @PutMapping("/users/money/deduct")
    void deductMoney(@RequestParam("pw") String pw, @RequestParam("amount") Integer amount);
}
