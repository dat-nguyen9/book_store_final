package com.book_store.service;

import com.book_store.entity.User;
import com.book_store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    public User getById(int id) {
        return userRepository.getById(id);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void sendVerificationEmail(User user, String siteUrl) throws MessagingException, UnsupportedEncodingException {
        String subject = "Congratulation";
        String senderName = "Đây là đồ án";
        String content = "<table style=\"width: 100% !important\" >\n" +
                "            <tbody>\n" +
                "                <tr>\n" +
                "                    <td>\n" +
                "                        <div>\n" +
                "                            <h2>Hello " + user.getUsername() + "</h2>\n" +
                "                        </div>\n" +
                "                        <div>\n" +
                "                            Gần đây bạn đã đăng ký tài khoản của mình trong hệ thống của chúng tôi. Chúng tôi rất vui vì bạn đã chọn cửa hàng của chúng tôi.\n" +
                "                        </div>\n" +
                "                        <br>\n" +

                "                        <br>\n" +
                "\n" +
                "                        <div>\n" +
                "                            Tận hưởng mua sắm\n.\n" +
                "                        </div>\n" +
                "\n" +
                "                        <br>\n" +
                "                        <div>\n" +
                "                            Trân trọng\n,\n" +
                "                            <h4>Đây là đồ án nè</h4>\n" +
                "                        </div>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "            </tbody>\n" +
                "        </table>";
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("EdulanSupport@gmail.com", senderName);
        helper.setTo(user.getCustomer().getEmail());
        helper.setSubject(subject);
        helper.setText(content, true);
        javaMailSender.send(message);
    }

    public void deleteUser(int id){
        userRepository.deleteById(id);
    }
}
