package org.foodOrdering.service;

import org.foodOrdering.dtos.MenuItemDTO;
import org.foodOrdering.mapper.MenuItemMapper;
import org.foodOrdering.model.MenuItem;
import org.foodOrdering.repositories.MenuItemRepository;
import org.foodOrdering.service.impl.MenuItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantMenuItemServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuItemMapper menuItemMapper;

    @InjectMocks
    private MenuItemServiceImpl menuItemService;

    private MenuItem menuItem1;
    private MenuItem menuItem2;
    private MenuItemDTO menuItemDTO1;
    private MenuItemDTO menuItemDTO2;

    @BeforeEach
    void setUp() {
        menuItem1 = MenuItem.builder()
                .id(1L)
                .itemName("Burger")
                .description("Delicious burger")
                .build();

        menuItem2 = MenuItem.builder()
                .id(2L)
                .itemName("Pizza")
                .description("Cheesy pizza")
                .build();

        menuItemDTO1 = new MenuItemDTO(1L, "Burger", "Delicious burger");
        menuItemDTO2 = new MenuItemDTO(2L, "Pizza", "Cheesy pizza");
    }

    @Test
    void testAddMenuItems_Success() {
        List<MenuItem> inputItems = List.of(menuItem1, menuItem2);
        List<MenuItem> existingItems = List.of(menuItem1);
        List<MenuItem> newItems = List.of(menuItem2);

        when(menuItemRepository.findByItemNameIn(anyList())).thenReturn(existingItems);
        when(menuItemRepository.saveAll(newItems)).thenReturn(newItems);
        when(menuItemMapper.toDTO(anyList())).thenReturn(List.of(menuItemDTO1, menuItemDTO2));

        List<MenuItemDTO> result = menuItemService.addMenuItems(inputItems);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(menuItemRepository, times(1)).findByItemNameIn(anyList());
        verify(menuItemRepository, times(1)).saveAll(newItems);
        verify(menuItemMapper, times(1)).toDTO(anyList());
    }

    @Test
    void testAddMenuItems_NoNewItems() {
        List<MenuItem> inputItems = List.of(menuItem1, menuItem2);
        List<MenuItem> existingItems = List.of(menuItem1, menuItem2);

        when(menuItemRepository.findByItemNameIn(anyList())).thenReturn(existingItems);
        when(menuItemMapper.toDTO(anyList())).thenReturn(List.of(menuItemDTO1, menuItemDTO2));

        List<MenuItemDTO> result = menuItemService.addMenuItems(inputItems);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(menuItemRepository, times(1)).findByItemNameIn(anyList());
        verify(menuItemRepository, never()).saveAll(anyList());
        verify(menuItemMapper, times(1)).toDTO(anyList());
    }

    @Test
    void testGetMenuItems_Success() {
        List<MenuItem> menuItems = List.of(menuItem1, menuItem2);
        List<MenuItemDTO> menuItemDTOs = List.of(menuItemDTO1, menuItemDTO2);

        when(menuItemRepository.findAll()).thenReturn(menuItems);
        when(menuItemMapper.toDTO(menuItems)).thenReturn(menuItemDTOs);

        List<MenuItemDTO> result = menuItemService.getMenuItems();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(menuItemRepository, times(1)).findAll();
        verify(menuItemMapper, times(1)).toDTO(menuItems);
    }
}
