-- ==============================================================================
-- Java Mini-Project Reference: Seed Sample Data
-- ==============================================================================

INSERT INTO student_attendance (attendance_id, student_name, course_code, attended_classes, total_classes, status)
VALUES
    (101, 'Aarav Sharma',   'BCA301', 45, 50, 'Eligible'),
    (102, 'Diya Patel',     'BCA301', 48, 50, 'Eligible'),
    (103, 'Rohan Verma',    'BCA302', 32, 50, 'Attendance Shortage'),
    (104, 'Ananya Iyer',    'BCA303', 42, 50, 'Eligible'),
    (105, 'Karthik Nair',   'BCA301', 28, 50, 'Attendance Shortage'),
    (106, 'Pooja Hegde',    'BCA304', 49, 50, 'Eligible')
ON CONFLICT (attendance_id) DO NOTHING;
