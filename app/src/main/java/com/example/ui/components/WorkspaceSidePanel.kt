package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalendarEventItem
import com.example.data.model.ContactItem
import com.example.data.model.KeepNoteItem
import com.example.data.model.TaskItem
import com.example.data.repository.EmailRepository
import com.example.ui.theme.StarGold

enum class WorkspaceTab(val title: String) {
    CALENDAR("Calendar"),
    KEEP("Keep"),
    TASKS("Tasks"),
    CONTACTS("Contacts"),
    ADDONS("Add-ons")
}

@Composable
fun WorkspaceSidePanel(
    repository: EmailRepository,
    activeEmailSubject: String? = null,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(WorkspaceTab.CALENDAR) }
    val calendarEvents by repository.calendarEvents.collectAsState()
    val keepNotes by repository.keepNotes.collectAsState()
    val tasks by repository.tasks.collectAsState()
    val contacts by repository.contacts.collectAsState()

    var newEventTitle by remember { mutableStateOf(activeEmailSubject?.let { "Review: $it" } ?: "") }
    var newNoteContent by remember { mutableStateOf(activeEmailSubject?.let { "Note on: $it" } ?: "") }
    var newTaskTitle by remember { mutableStateOf(activeEmailSubject?.let { "Follow up on: $it" } ?: "") }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = modifier
            .fillMaxHeight()
            .width(340.dp)
            .testTag("workspace_side_panel")
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Extension, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                    }
                    Text("Google Workspace", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp).testTag("close_workspace_panel")) {
                    Icon(Icons.Default.Close, contentDescription = "Close Workspace Panel")
                }
            }

            // Tab Navigation
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                WorkspaceTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            // Tab Content
            Box(modifier = Modifier.weight(1f).padding(12.dp)) {
                when (selectedTab) {
                    WorkspaceTab.CALENDAR -> CalendarTabContent(
                        events = calendarEvents,
                        activeSubject = activeEmailSubject,
                        onAddEvent = { title, time, date, loc ->
                            repository.addCalendarEvent(title, time, date, loc)
                        }
                    )
                    WorkspaceTab.KEEP -> KeepTabContent(
                        notes = keepNotes,
                        activeSubject = activeEmailSubject,
                        onAddNote = { title, content ->
                            repository.addKeepNote(title, content)
                        }
                    )
                    WorkspaceTab.TASKS -> TasksTabContent(
                        tasks = tasks,
                        activeSubject = activeEmailSubject,
                        onAddTask = { title, due ->
                            repository.addTask(title, due)
                        },
                        onToggleTask = { id ->
                            repository.toggleTask(id)
                        }
                    )
                    WorkspaceTab.CONTACTS -> ContactsTabContent(contacts = contacts)
                    WorkspaceTab.ADDONS -> AddonsTabContent()
                }
            }
        }
    }
}

@Composable
fun CalendarTabContent(
    events: List<CalendarEventItem>,
    activeSubject: String?,
    onAddEvent: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(activeSubject?.let { "Review: $it" } ?: "") }
    var time by remember { mutableStateOf("11:00 AM - 11:30 AM") }
    var date by remember { mutableStateOf("Tomorrow") }
    var showAddForm by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxHeight()) {
        if (activeSubject != null && !showAddForm) {
            Button(
                onClick = { showAddForm = true },
                modifier = Modifier.fillMaxWidth().testTag("button_convert_to_calendar_event")
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Convert Email to Event", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (showAddForm) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("New Calendar Event", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Time Range") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showAddForm = false }) { Text("Cancel") }
                        Button(onClick = {
                            if (title.isNotBlank()) {
                                onAddEvent(title, time, date, "Google Meet")
                                showAddForm = false
                            }
                        }) { Text("Save Event") }
                    }
                }
            }
        }

        Text("Upcoming Events:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(events) { event ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(event.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            if (event.isFromEmail) {
                                Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                    Text("From Mail", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text("${event.date} • ${event.timeRange}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("📍 ${event.location}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun KeepTabContent(
    notes: List<KeepNoteItem>,
    activeSubject: String?,
    onAddNote: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(activeSubject?.let { "Note: $it" } ?: "") }
    var content by remember { mutableStateOf("") }
    var showAddForm by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxHeight()) {
        if (!showAddForm) {
            Button(
                onClick = { showAddForm = true },
                modifier = Modifier.fillMaxWidth().testTag("button_save_email_to_keep")
            ) {
                Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Take a Note / Save Email", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (showAddForm) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Note...") }, modifier = Modifier.fillMaxWidth().height(80.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showAddForm = false }) { Text("Cancel") }
                        Button(onClick = {
                            if (title.isNotBlank()) {
                                onAddNote(title, content)
                                showAddForm = false
                            }
                        }) { Text("Save Note") }
                    }
                }
            }
        }

        Text("Your Google Keep Notes:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notes) { note ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(note.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(note.content, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun TasksTabContent(
    tasks: List<TaskItem>,
    activeSubject: String?,
    onAddTask: (String, String) -> Unit,
    onToggleTask: (String) -> Unit
) {
    var newTaskTitle by remember { mutableStateOf(activeSubject?.let { "Review $it" } ?: "") }
    var newTaskDue by remember { mutableStateOf("Tomorrow, 5:00 PM") }

    Column(modifier = Modifier.fillMaxHeight()) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("Add Task from Email", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    label = { Text("Task title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_task_title")
                )
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            onAddTask(newTaskTitle, newTaskDue)
                            newTaskTitle = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("button_add_task")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add to Google Tasks", fontSize = 12.sp)
                }
            }
        }

        Text("Google Tasks:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(tasks) { task ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().clickable { onToggleTask(task.id) }
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = task.isCompleted, onCheckedChange = { onToggleTask(task.id) })
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                task.title,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            )
                            Text("Due: ${task.dueDate}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactsTabContent(contacts: List<ContactItem>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(contacts) { contact ->
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(getAvatarColor(contact.name)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(contact.name.take(1), fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(contact.email, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        Text(contact.role, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (contact.isVip) {
                        Icon(Icons.Default.Star, contentDescription = "VIP Contact", tint = StarGold, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AddonsTabContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Trello Add-on", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Quickly create a Trello card on your engineering board directly from this email.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* simulated trello export */ }, modifier = Modifier.height(32.dp)) {
                    Text("Export to Trello Card", fontSize = 11.sp)
                }
            }
        }

        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Zoom Meeting Add-on", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Instantly generate a 1-click Zoom meeting link and append it to your reply.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = { /* simulated zoom link */ }, modifier = Modifier.height(32.dp)) {
                    Text("Insert Zoom Link", fontSize = 11.sp)
                }
            }
        }
    }
}
