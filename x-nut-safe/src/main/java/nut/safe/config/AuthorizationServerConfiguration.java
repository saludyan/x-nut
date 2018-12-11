package nut.safe.config;

import nut.jpa.component.AppCtxProvider;
import nut.safe.XClientDetailInterface;
import nut.safe.XUserDetailsInterface;
import nut.safe.component.XPasswordEncoder;
import nut.safe.exceptions.XSafeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * 认证服务
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private XPasswordEncoder xPasswordEncoder;


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        ClientDetailsService clientDetailsService = AppCtxProvider.getBean(XClientDetailInterface.class);

        if(clientDetailsService == null){
            throw new XSafeException("请实现「 XClientDetailInterface 」接口! ヽ(`З’)ﾉ ");
        }
        clients.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {


        UserDetailsService userDetailsService = AppCtxProvider.getBean(XUserDetailsInterface.class);
        if(userDetailsService == null){
            throw new XSafeException("请实现「 XUserDetailsInterface 」接口! ヽ(`З’)ﾉ ");
        }

        endpoints
                .userDetailsService(userDetailsService)
                .tokenStore(new RedisTokenStore(redisConnectionFactory))
                .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        //允许表单认证
        oauthServer.allowFormAuthenticationForClients();
        oauthServer.passwordEncoder(xPasswordEncoder);
    }

}
