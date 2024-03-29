package com.kosta.sbproject.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.extern.java.Log;

@Log
@Configuration        
@EnableWebSecurity    //security설정을 담당한다. Bean이다.
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	@Autowired
	MemberService memberService; 

	
//암호화 하는 부분.....빈으로 만들어주기만 하면된다. 
	@Bean     
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();   //Spring Security에서 제공하는 비밀번호 암호화 객체
	}
	
	
	//아래 오버라이드 된부분은 WebSecurityConfigurerAdapter에서 가져온것....
	
	@Override  //WebSecurity를 통해 HTTP 요청에 대한 웹 기반 보안을 구성	
//무시해야하는 것들을 설정해주겠다는 의미 web에 ignoring
	public void configure(WebSecurity web) throws Exception {
		// 파일 기준은 resources/static 디렉터리의 하위 파일 목록은 인증 무시 ( = 항상통과 )
		web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/lib/**");
	}
	
//인증과 인가를 주는 부분
	protected void configure(HttpSecurity http) throws Exception {
		log.info("security config..........");
		// antMatchers url 패턴에 대한 접근허용
		// permitAll: 모든사용자가 접근가능하다는 의미
		// hasRole : 특정권한을 가진 사람만 접근가능하다는 의미
		http.authorizeRequests() // HttpServletRequest에 따라 접근(access)을 제한
		/*인증안해도됨*/	
				.antMatchers("/hello/**", "/auth/**").permitAll() //   누구나 접근 허용
		/* 인가부분role을 가져야 확인가능 */			
				.antMatchers("/admin/**").hasRole("ADMIN") // /admin으로 시작하는 경로는  ADMIN롤을 가진 사용자만  접근 가능(자동으로 ROLE_가 삽입)
				.antMatchers("/manager/**").hasRole("MANAGER")
				.anyRequest().authenticated() // 나머지 요청들은 권한의 종류에 상관 없이 권한이 있어야 접근 가능
		/* 로그인 페이지 사용할건데 페이지 경로지정부분 */	
				.and().formLogin() // form 기반으로 인증을 하도록 한다. 로그인 정보는 기본적으로 HttpSession을 이용
				.loginPage("/auth/login") // 로그인 페이지 링크 .... post의 이름이 같다면 loginProcessingUrl생략 
		/* 로그인 성공하면 보여줄곳 */		                                            //스프링시큐리티가 해당주소로 오는 요청을 가로채서 대신한다. 
				.defaultSuccessUrl("/loginSuccess") // 로그인 성공 후 리다이렉트 주소				
				.permitAll()
		/* 로그 아웃, and안쓸꺼면 이전에서();로 마무리하고 http.logout()로 시작하면된다... */	
				.and()
				.logout() // 로그아웃에 관한 설정을 의미
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/auth/login") // 로그아웃 성공시 리다이렉트 주소
				.invalidateHttpSession(true)  // 세션 지우기
				
		/* 토큰쓸려면 아랫줄 막으면됨, 현재는 테스트할때 계속 토큰사용하면 불편해서 비활성화 */
				.and().csrf().disable();  //csrf(크로스사이트 위조요청에 대한 설정) 토큰 비활성화 (test시에는 disable권장)            
		http.exceptionHandling().accessDeniedPage("/accessDenied"); // 403 예외처리 핸들링   권한이 없는 대상이 접속을시도했을 때
	}
}
