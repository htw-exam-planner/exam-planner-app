package models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import repository.DBRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static testsupport.TestSupport.setStaticValue;

@RunWith(MockitoJUnitRunner.class)
public class GroupTest {
    @Mock
    private DBRepository dbRepositoryMock;

    @Captor
    private ArgumentCaptor<Group> groupCaptor;

    @Before
    public void setUp() throws Exception {
        setStaticValue(DBRepository.class, "instance", dbRepositoryMock);
    }

    @Test
    public void shouldReturnDatabaseDataWhenAccessingAll() throws Exception {
        List<Group> resultList = Collections.emptyList();
        when(dbRepositoryMock.getGroups()).thenReturn(resultList);

        List<Group> result = Group.all();

        assertThat(result).usingFieldByFieldElementComparator().isEqualTo(resultList);
    }

    @Test
    public void shouldGenerateCorrectGroups() throws Exception {
        final int NUMBER_OF_GROUPS = 15;

        Group.generate(NUMBER_OF_GROUPS);

        verify(dbRepositoryMock, times(1)).deleteAllGroups();

        inOrder(dbRepositoryMock).verify(dbRepositoryMock, times(NUMBER_OF_GROUPS)).insertGroup(groupCaptor.capture());

        List<Group> insertedGroups = groupCaptor.getAllValues();
        IntStream.range(1, NUMBER_OF_GROUPS+1).forEach(groupNumber ->  {
            Group group = insertedGroups.get(groupNumber - 1);
            assertThat(group.getNumber()).isEqualTo(groupNumber);
        });
    }

    @Test
    public void shouldThrowIfLessThanOneGroupShouldBeGenerated() throws Exception {
        assertThatThrownBy(() -> Group.generate(0)).isInstanceOf(IllegalArgumentException.class);

        verify(dbRepositoryMock, never()).deleteAllGroups();
        verify(dbRepositoryMock, never()).insertGroup(any());
    }

    @Test
    public void shouldDeleteGroupInDatabase() throws Exception {
        final Group group = new Group(3);

        Group.delete(group);

        verify(dbRepositoryMock, only()).deleteGroup(groupCaptor.capture());
        assertThat(groupCaptor.getValue()).isEqualTo(group);
    }

    @Test
    public void shouldCreateANewGroupCorrectlyIfGroupsAreConsecutive() throws Exception {
        final int NEXT_GROUP_NUMBER = 4;
        final List<Group> groups = IntStream.range(1, NEXT_GROUP_NUMBER).mapToObj(Group::new).collect(Collectors.toList());
        when(dbRepositoryMock.getGroups()).thenReturn(groups);

        final Group result = Group.create();

        verify(dbRepositoryMock, times(1)).insertGroup(groupCaptor.capture());
        assertThat(groupCaptor.getValue().getNumber()).isEqualTo(NEXT_GROUP_NUMBER);
        assertThat(result.getNumber()).isEqualTo(NEXT_GROUP_NUMBER);
    }

    @Test
    public void shouldCreateANewGroupCorrectlyIfGroupsAreNotConsecutive() throws Exception {
        final int NEXT_GROUP_NUMBER = 4;
        final List<Group> groups = IntStream.range(1, NEXT_GROUP_NUMBER).filter(i -> i != 2).mapToObj(Group::new).collect(Collectors.toList());
        when(dbRepositoryMock.getGroups()).thenReturn(groups);

        final Group result = Group.create();

        verify(dbRepositoryMock, times(1)).insertGroup(groupCaptor.capture());
        assertThat(result.getNumber()).isEqualTo(NEXT_GROUP_NUMBER);
        assertThat(groupCaptor.getValue()).isEqualTo(result);
    }

    @Test
    public void equalsShouldCompareGroupNumbers() {
        assertThat(new Group(1).equals(new Group(1))).isTrue();
        assertThat(new Group(1).equals(new Group(2))).isFalse();
    }
}