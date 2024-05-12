package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.controller.ICustomerService;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.CustomerDetailsDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.client.CardsFeignClient;
import com.eazybytes.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerDetailsServiceImpl implements ICustomerService {

    private final AccountsRepository accountsRepository;
    private final CustomerRepository customerRepository;
    private final LoansFeignClient loansFeignClient;
    private final CardsFeignClient cardsFeignClient;


    /**
     * @param mobileNumber - Input Mobile Number
     * @return Accounts Details based on a given mobileNumber
     */
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        var customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        var accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());

        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        var loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        if (loansDtoResponseEntity != null)
            customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());

        var cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        if (cardsDtoResponseEntity != null)
            customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailsDto;
    }


}