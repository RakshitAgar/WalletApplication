package com.example.WalletApplication.service;

import com.example.WalletApplication.grpc.ConversionServiceGrpc;
import com.example.WalletApplication.grpc.ConvertRequest;
import com.example.WalletApplication.grpc.ConvertResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Slf4j
@Service
public class CurrencyConversionService {

    private ManagedChannel channel;
    private ConversionServiceGrpc.ConversionServiceBlockingStub blockingStub;

    @Value("${grpc.server.host:localhost}")
    private String host;

    @Value("${grpc.server.port:50051}")
    private int port;

    @PostConstruct
    private void init() {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = ConversionServiceGrpc.newBlockingStub(channel);
        log.info("gRPC client initialized, connecting to {}:{}", host, port);
    }

    public double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        try {
            ConvertRequest request = ConvertRequest.newBuilder()
                    .setAmount(amount)
                    .setFromCurrency(fromCurrency)
                    .setToCurrency(toCurrency)
                    .build();

            ConvertResponse response = blockingStub.convert(request);
            return response.getConvertedAmount();
        } catch (Exception e) {
            log.error("Error converting currency: {} {} to {}", amount, fromCurrency, toCurrency, e);
            throw new RuntimeException("Currency conversion failed", e);
        }
    }

    @PreDestroy
    private void cleanup() {
        if (channel != null) {
            channel.shutdown();
        }
    }
}