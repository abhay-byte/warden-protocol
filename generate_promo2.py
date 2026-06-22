import os
import urllib.request
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import textwrap
import glob

out_dir = "docs/storelisting"
os.makedirs(out_dir, exist_ok=True)

# Download fonts
font_title_path = "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"
font_desc_path = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"

font_title = ImageFont.truetype(font_title_path, 55)
font_desc = ImageFont.truetype(font_desc_path, 35)
font_fg_title = ImageFont.truetype(font_title_path, 70)
font_fg_desc = ImageFont.truetype(font_desc_path, 35)

icon_path = "assets/icon.png"
bg_paths = glob.glob("assets/location_hero_sources/*.png") + glob.glob("assets/event_hero_sources/*.png")

screenshots = {
    1: {"file": "fastlane/metadata/android/en-US/images/1.png", "title": "WARDEN CORE HUB", "desc": "Monitor your 1,000 survivors in real-time. Make choices that shape humanity's future."},
    2: {"file": "fastlane/metadata/android/en-US/images/2.png", "title": "ARCHIVE LOGS", "desc": "Preserve cultural data and technology across multiple runs."},
    3: {"file": "fastlane/metadata/android/en-US/images/3.png", "title": "LONG-RANGE CHRONICLE", "desc": "Read generative AI epilogues predicting settlement success over 100 years."},
    4: {"file": "fastlane/metadata/android/en-US/images/4.png", "title": "SYSTEM CONFIG", "desc": "Manage critical bunker subsystems and power routing under extreme pressure."},
    5: {"file": "fastlane/metadata/android/en-US/images/5.png", "title": "SURFACE SCAN", "desc": "Deploy probes to explore procedurally generated post-apocalyptic sites."},
    6: {"file": "fastlane/metadata/android/en-US/images/6.png", "title": "CRITICAL EVENT", "desc": "Respond to apex threats and unexpected hazards. Every choice has hidden consequences."},
    7: {"file": "fastlane/metadata/android/en-US/images/7.png", "title": "PROTOCOL RESPONSE", "desc": "Take calculated risks with brutalist tactical oversight."},
    8: {"file": "fastlane/metadata/android/en-US/images/8.png", "title": "COMMAND RESPONSE", "desc": "Live terminal directives that react to your vault's decaying status."}
}

# Colors matching docs/design.md
color_bg = (13, 17, 23)
color_amber = (255, 176, 0)
color_green = (0, 255, 65)
color_panel = (22, 27, 34)

def draw_scanlines(draw, width, height):
    for y in range(0, height, 4):
        draw.line([(0, y), (width, y)], fill=(0, 0, 0, 40), width=1)

def create_promo_image(idx, data):
    base = Image.new("RGB", (1920, 1080), color_bg)
    
    # Left side background feature graphic subtly blended
    bg_file = bg_paths[(idx - 1) % len(bg_paths)]
    try:
        bg = Image.open(bg_file).convert("RGBA")
        # Resize to cover
        bg_aspect = bg.width / bg.height
        new_w = int(1080 * bg_aspect)
        bg = bg.resize((new_w, 1080), Image.Resampling.LANCZOS)
        # Crop left
        bg = bg.crop((0, 0, 1920, 1080))
        # Mask with gradient
        mask = Image.new("L", (1920, 1080), 0)
        mask_draw = ImageDraw.Draw(mask)
        for x in range(1920):
            alpha = max(0, 255 - int((x / 1920) * 500)) # Fade out strongly to the right
            mask_draw.line([(x, 0), (x, 1080)], fill=alpha)
        
        base.paste(bg, (0, 0), mask)
    except Exception as e:
        print("BG err:", e)
        
    draw = ImageDraw.Draw(base, "RGBA")
    
    # Brutalist elements
    # Panel for text
    draw.rectangle([(80, 250), (1200, 850)], fill=(0, 0, 0, 180), outline=color_panel, width=2)
    # Accent line
    draw.rectangle([(80, 250), (90, 850)], fill=color_amber)
    # Target reticle decor
    draw.line([(1150, 260), (1180, 260)], fill=color_green, width=3)
    draw.line([(1180, 260), (1180, 290)], fill=color_green, width=3)
    
    text_x = 120
    text_y = 350
    
    if idx == 1:
        try:
            icon = Image.open(icon_path).convert("RGBA")
            icon = icon.resize((150, 150), Image.Resampling.LANCZOS)
            base.paste(icon, (text_x, 150), icon)
            text_y = 350
        except Exception as e:
            pass
            
    # Text wrapping
    title = data["title"]
    desc = data["desc"]
    
    draw.text((text_x, text_y), f"// {title}", fill=color_amber, font=font_title)
    
    wrapped_desc = textwrap.wrap(desc, width=35)
    dy = text_y + 120
    for line in wrapped_desc:
        draw.text((text_x, dy), line, fill=(220, 220, 220), font=font_desc)
        dy += 60

    # Draw screenshot panel on the right
    ss_x = 1350
    ss_y = 100
    ss_h = 880
    
    # Outer frame
    draw.rectangle([(ss_x - 10, ss_y - 10), (ss_x + 505, ss_y + ss_h + 10)], fill=(0,0,0,200), outline=color_green, width=2)
    # "TERMINAL" label
    draw.text((ss_x, ss_y - 50), "[ SYSTEM TERMINAL ]", fill=color_green, font=ImageFont.truetype(font_desc_path, 25))

    try:
        ss = Image.open(data["file"]).convert("RGBA")
        ss_ratio = ss.width / ss.height
        ss_w = int(ss_h * ss_ratio) # Should be around 440-450
        ss = ss.resize((ss_w, ss_h), Image.Resampling.LANCZOS)
        base.paste(ss, (ss_x + (450 - ss_w)//2, ss_y), ss)
    except Exception as e:
        print("SS err:", e)
        
    # Overlay scanlines removed for better GIF compression
    
    base.save(f"{out_dir}/promo_{idx}.png")
    print(f"Created promo_{idx}.png")

for idx, data in screenshots.items():
    create_promo_image(idx, data)

# Feature Graphic 1024x500
fg = Image.new("RGB", (1024, 500), color_bg)
try:
    bg_fg = Image.open(bg_paths[0]).convert("RGBA")
    bg_fg = bg_fg.resize((1024, int(1024 / (bg_fg.width/bg_fg.height))), Image.Resampling.LANCZOS)
    bg_fg = bg_fg.crop((0, (bg_fg.height - 500)//2, 1024, (bg_fg.height + 500)//2))
    
    mask_fg = Image.new("L", (1024, 500), 0)
    mdraw = ImageDraw.Draw(mask_fg)
    for x in range(1024):
        alpha = max(0, 255 - int((x / 1024) * 400))
        mdraw.line([(x, 0), (x, 500)], fill=alpha)
    fg.paste(bg_fg, (0,0), mask_fg)
except:
    pass

draw_fg = ImageDraw.Draw(fg, "RGBA")
draw_fg.rectangle([(40, 40), (50, 460)], fill=color_amber)
draw_fg.line([(960, 40), (980, 40)], fill=color_green, width=3)
draw_fg.line([(980, 40), (980, 60)], fill=color_green, width=3)

try:
    icon = Image.open(icon_path).convert("RGBA")
    icon = icon.resize((150, 150), Image.Resampling.LANCZOS)
    fg.paste(icon, (80, 80), icon)
except:
    pass

draw_fg.text((80, 260), "THE WARDEN PROTOCOL", fill=color_amber, font=font_fg_title)
draw_fg.text((80, 340), "Post-Apocalyptic Vault Strategy", fill=color_green, font=font_fg_desc)

# draw_scanlines(draw_fg, 1024, 500)
fg.save(f"{out_dir}/feature_graphic.png")

fg_169 = Image.new("RGB", (1920, 1080), color_bg)
fg_resized = fg.resize((1920, int(1920 * (500/1024))), Image.Resampling.LANCZOS)
fg_169.paste(fg_resized, (0, (1080 - fg_resized.height)//2))
fg_169.save(f"{out_dir}/feature_graphic_169.png")

print("Done generating designed images.")
