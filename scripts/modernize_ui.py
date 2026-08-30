from pathlib import Path
p=Path('app/src/main/java/com/ahmed/teacher/MainActivity.java')
t=p.read_text(encoding='utf-8')
# Final source-level marker for the requested grading rules.
# The build workflow applies the existing UI transformation; this script validates the source before build.
required=['exam1_','exam2_','private void monthly()','private void assessment()']
missing=[x for x in required if x not in t]
if missing:
    raise SystemExit('Missing source sections: '+', '.join(missing))
print('Teacher source validated for monthly/exam and weekly assessment sections')
