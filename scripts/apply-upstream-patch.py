#!/usr/bin/env python3
import sys
import os
import subprocess
import re
import tempfile

def print_help():
    print("Usage: python3 scripts/apply-upstream-patch.py <commit-hash-or-patch-file>")
    print("Apply a commit or patch from the upstream master branch to the 8.x branch,")
    print("translating paths and packages to fluentQ namespaces.")
    print("\nExamples:")
    print("  python3 scripts/apply-upstream-patch.py a1b2c3d")
    print("  python3 scripts/apply-upstream-patch.py my-bugfix.patch")

def translate_paths_in_line(line):
    # Replace querydsl-libraries/querydsl- with fluentq-libraries/fluentq-
    line = re.sub(r'\bquerydsl-libraries/querydsl-', 'fluentq-libraries/fluentq-', line)
    # Replace querydsl-tooling/querydsl- with fluentq-tooling/fluentq-
    line = re.sub(r'\bquerydsl-tooling/querydsl-', 'fluentq-tooling/fluentq-', line)
    # Replace package directories in file paths
    line = line.replace('com/querydsl', 'fluentq')
    line = line.replace('io/github/openfeign/querydsl', 'fluentq')
    return line

def translate_content(filename, content):
    if filename.endswith('pom.xml'):
        # POM translations
        content = content.replace('<groupId>com.querydsl</groupId>', '<groupId>io.github.openfeign.fluentq</groupId>')
        content = content.replace('<groupId>io.github.openfeign.querydsl</groupId>', '<groupId>io.github.openfeign.fluentq</groupId>')
        content = re.sub(r'<artifactId>querydsl-([^<]+)</artifactId>', r'<artifactId>fluentq-\1</artifactId>', content)
        content = re.sub(r'\bquerydsl-libraries/querydsl-', 'fluentq-libraries/fluentq-', content)
        content = re.sub(r'\bquerydsl-tooling/querydsl-', 'fluentq-tooling/fluentq-', content)
        content = content.replace('com.querydsl', 'fluentq')
        content = content.replace('io.github.openfeign.querydsl', 'fluentq')
        content = content.replace('Querydsl', 'FluentQ')
        content = content.replace('querydsl', 'fluentq')
        return content
    else:
        # Code/resource translations
        content = content.replace('com.querydsl', 'fluentq')
        content = content.replace('io.github.openfeign.querydsl', 'fluentq')
        content = content.replace('Querydsl', 'FluentQ')
        content = content.replace('querydsl', 'fluentq')
        return content

def translate_patch(patch_text):
    # Split the patch by the start of each file diff
    blocks = re.split(r'^(diff --git .*)', patch_text, flags=re.MULTILINE)
    
    translated_blocks = []
    # The first element is metadata before the first diff block
    if blocks and not blocks[0].startswith('diff --git'):
        translated_blocks.append(blocks[0])
        blocks = blocks[1:]
        
    for i in range(0, len(blocks), 2):
        if i + 1 >= len(blocks):
            translated_blocks.append(blocks[i])
            break
        header = blocks[i]
        body = blocks[i+1]
        
        # Extract original file path
        match = re.search(r'a/(\S+)', header)
        if not match:
            match = re.search(r'--git a/(\S+)', header)
            
        is_source_module = False
        original_path = ""
        if match:
            original_path = match.group(1)
            is_source_module = original_path.startswith('querydsl-libraries/') or original_path.startswith('querydsl-tooling/')
            
        if is_source_module:
            header = translate_paths_in_line(header)
            
            translated_body_lines = []
            for line in body.splitlines():
                if line.startswith('--- ') or line.startswith('+++ ') or line.startswith('rename ') or line.startswith('similarity '):
                    line = translate_paths_in_line(line)
                elif line.startswith('@@ '):
                    pass
                else:
                    if line:
                        line_type = line[0]
                        line_content = line[1:]
                        line_content = translate_content(original_path, line_content)
                        line = line_type + line_content
                translated_body_lines.append(line)
            body = '\n'.join(translated_body_lines) + '\n'
            
        translated_blocks.append(header + body)
        
    return ''.join(translated_blocks)

def main():
    if len(sys.argv) < 2 or sys.argv[1] in ('-h', '--help'):
        print_help()
        sys.exit(0)
        
    target = sys.argv[1]
    
    # 1. Obtain the patch content
    if os.path.isfile(target):
        print(f"Reading patch from file: {target}")
        with open(target, 'r', encoding='utf-8') as f:
            patch_text = f.read()
    else:
        print(f"Obtaining patch for commit/ref: {target}")
        try:
            # Run git show
            patch_text = subprocess.check_output(['git', 'show', target], stderr=subprocess.STDOUT).decode('utf-8')
        except subprocess.CalledProcessError as e:
            print(f"Error running 'git show {target}':")
            print(e.output.decode('utf-8'))
            sys.exit(1)
            
    # 2. Translate patch
    print("Translating paths and package namespaces to fluentQ...")
    translated_patch = translate_patch(patch_text)
    
    # 3. Write to temp file and apply
    with tempfile.NamedTemporaryFile(mode='w', suffix='.patch', delete=False, encoding='utf-8') as temp:
        temp.write(translated_patch)
        temp_path = temp.name
        
    print(f"Applying translated patch to the working tree...")
    try:
        subprocess.check_call(['git', 'apply', temp_path])
        print("Patch applied successfully!")
    except subprocess.CalledProcessError as e:
        print("Error: Failed to apply the translated patch.")
        print("Please check the generated patch file at:")
        print(f"  {temp_path}")
        sys.exit(1)
        
    # Clean up temp file on success
    try:
        os.remove(temp_path)
    except OSError:
        pass
        
    # 4. Regenerate compatibility wrappers
    print("Regenerating compatibility wrappers...")
    try:
        subprocess.check_call([sys.executable, 'scripts/generate-compat-wrappers.py'])
        print("Compatibility wrappers regenerated successfully!")
    except subprocess.CalledProcessError as e:
        print("Error: Failed to regenerate compatibility wrappers.")
        sys.exit(1)
        
    print("\nWorkflow completed successfully!")
    print("You can verify the build by running:")
    print("  ./mvnw -Pquickbuild clean install")

if __name__ == '__main__':
    main()
