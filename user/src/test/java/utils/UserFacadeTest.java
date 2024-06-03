package utils;


import database.UserRepository;
import entity.Authority;
import exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import usecase.AuthorityService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserFacadeTest {

    UserFacade userFacade;

    @Mock
    private AuthorityService authorityService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        userFacade = new UserFacade(authorityService, userRepository);
    }

    Authority admin = new Authority("ROLE_ADMINISTRATOR", "ADMINISTRATIVE");
    Authority accountant = new Authority("ROLE_ACCOUNTANT", "USER");
    Authority user = new Authority("ROLE_USER", "USER");

    @Test
    @DisplayName("getAuthorityFromRole() - Returns associated Authority entity, role exists in db")
    void givenExistingRole_whenGettingAuthorityFromRole_thenReturnAuthorityEntity() throws Exception {
        String role = "ROLE_ADMINISTRATOR";
        when(authorityService.getAuthoritybyRole(role)).thenReturn(admin);
        Authority actual = userFacade.getAuthorityFromRole(role);
        assertEquals(admin, actual);
    }

    @Test
    @DisplayName("getAuthorityFromRole() - Role does not exist in DB, throws 'NotFoundException'")
    void givenNonExistentRole_whenGettingAuthorityFromRole_thenThrowNotFoundException() throws Exception {
        String role = "ROLE_SENIOR_PROGRAM_MANAGER";
        when(authorityService.getAuthoritybyRole(role)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> userFacade.getAuthorityFromRole(role));
    }

    //roleExists() -> Unit testing this method would be trivial as this method calls a method in authorityService
    //which makes a call to the AuthorityRepository later.

    @Test
    @DisplayName("getDefaultAuthority() - Returns 'Admin' if no pre-existing users")
    void givenZeroExistingUsers_whenGettingDefaultAuthority_thenReturnAdminAuthority() throws Exception {
        when(userRepository.count()).thenReturn(0L);
        when(authorityService.getDefaultAuthority(0L)).thenReturn(admin);
        Authority actualAuthority = userFacade.getDefaultAuthority();
        assertEquals(admin, actualAuthority);
    }

    @Test
    @DisplayName("getDefaultAuthority() - Returns 'Users' if there are pre-existing users")
    void givenMoreThanZeroExistingUsers_whenGettingDefaultAuthority_thenReturnAdminAuthority() throws Exception{
        when(userRepository.count()).thenReturn(2L);
        when(authorityService.getDefaultAuthority(2L)).thenReturn(user);
        Authority actualAuthority = userFacade.getDefaultAuthority();
        assertEquals(user, actualAuthority);
    }
}




